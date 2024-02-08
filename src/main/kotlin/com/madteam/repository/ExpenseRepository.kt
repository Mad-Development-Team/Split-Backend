package com.madteam.repository

import com.madteam.data.model.*
import com.madteam.data.table.*
import com.madteam.getCurrentDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ExpenseRepository {

    fun createExpense(newExpense: Expense): Expense {
        val insertedId = transaction {
            ExpenseTable.insertAndGetId {
                it[expenseTitle] = newExpense.expenseTitle
                it[expenseDescription] = newExpense.expenseDescription
                it[totalAmount] = newExpense.totalAmount
                it[images] = newExpense.images?.joinToString(",")
                it[paymentMethod] = newExpense.paymentMethod
                it[createdDate] = getCurrentDateTime()
                it[groupId] = newExpense.groupId
                it[currency] = newExpense.currency.currency
                it[expenseType] = newExpense.expenseType.id
            }
        }
        try {
            transaction {
                newExpense.paidBy.forEach { paidBy ->
                    PaidByExpenseTable.insert {
                        it[expenseId] = insertedId.value
                        it[memberId] = paidBy.memberId
                        it[paidAmount] = paidBy.amount
                    }
                }
            }
        } catch (e: Exception) {
            transaction {
                ExpenseTable.deleteWhere { ExpenseTable.id eq insertedId.value }
            }
            throw e
        }
        try {
            transaction {
                newExpense.forWhom.forEach { forWhom ->
                    MemberExpensesTable.insert {
                        it[expenseId] = insertedId.value
                        it[memberId] = forWhom.memberId
                        it[paidAmount] = forWhom.amount
                    }
                }
            }
        } catch (e: Exception) {
            transaction {
                ExpenseTable.deleteWhere { ExpenseTable.id eq insertedId.value }
                PaidByExpenseTable.deleteWhere { expenseId eq insertedId.value }
            }
            throw e
        }
        return getExpenseById(insertedId.value) ?: throw IllegalStateException("Expense not found after creation")
    }

    fun createExpenseType(newExpenseType: ExpenseType): Int {
        val expenseTypeId =  transaction {
            ExpenseTypeTable.insertAndGetId {
                it[title] = newExpenseType.title
                it[icon] = newExpenseType.icon
                it[group] = newExpenseType.group
            }
        }
        return expenseTypeId.value
    }

    fun deleteExpenseType(id: Int) {
        transaction {
            ExpenseTypeTable.deleteWhere { ExpenseTypeTable.id eq id }
        }
    }

    private fun getExpenseById(id: Int): Expense? {
        return transaction {
            ExpenseTable.selectAll().where { ExpenseTable.id eq id }
                .mapNotNull { toExpense(it) }
                .singleOrNull()
        }
    }

    private fun toExpense(row: ResultRow): Expense {
        val expenseId = row[ExpenseTable.id].value

        val paidByList = transaction {
            PaidByExpenseTable.selectAll().where { PaidByExpenseTable.expenseId eq expenseId }
                .map { paidByRow ->
                    PaidBy(
                        memberId = paidByRow[PaidByExpenseTable.memberId],
                        amount = paidByRow[PaidByExpenseTable.paidAmount],
                        expenseId = expenseId
                    )
                }
        }

        val forWhomList = transaction {
            MemberExpensesTable.selectAll().where { MemberExpensesTable.expenseId eq expenseId }
                .map { forWhomRow ->
                    MemberExpenses(
                        memberId = forWhomRow[MemberExpensesTable.memberId],
                        amount = forWhomRow[MemberExpensesTable.paidAmount],
                        expenseId = expenseId
                    )
                }
        }

        return Expense(
            id = expenseId,
            expenseTitle = row[ExpenseTable.expenseTitle],
            expenseDescription = row[ExpenseTable.expenseDescription],
            totalAmount = row[ExpenseTable.totalAmount],
            expenseType = getExpenseType(row[ExpenseTable.expenseType]),
            createdDate = row[ExpenseTable.createdDate],
            groupId = row[ExpenseTable.groupId],
            currency = getCurrency(row[ExpenseTable.currency]),
            paymentMethod = row[ExpenseTable.paymentMethod],
            images = row[ExpenseTable.images]?.split(","),
            paidBy = paidByList,
            forWhom = forWhomList
        )
    }


    private fun getExpenseType(id: Int): ExpenseType {
        return transaction {
            ExpenseTypeTable.selectAll().where { ExpenseTypeTable.id eq id }
                .mapNotNull { toExpenseType(it) }
                .singleOrNull() ?: throw IllegalStateException("ExpenseType not found")
        }
    }

    private fun toExpenseType(row: ResultRow): ExpenseType =
        ExpenseType(
            id = row[ExpenseTypeTable.id].value,
            title = row[ExpenseTypeTable.title],
            icon = row[ExpenseTypeTable.icon],
            group = row[ExpenseTypeTable.group]
        )

    private fun getCurrency(code: String): Currency {
        return CurrencyRepository().getCurrencyByCode(code) ?: throw IllegalStateException("Currency not found")
    }

    fun getGroupExpenses(groupId: Int): List<Expense> {
        return transaction {
            ExpenseTable.selectAll().where { ExpenseTable.groupId eq groupId }
                .mapNotNull { toExpense(it) }
        }
    }

    fun deleteExpense(id: Int) {
        transaction {
            ExpenseTable.deleteWhere { ExpenseTable.id eq id }
        }
    }

    fun updateBalances(balances: List<Balance>) {
        val groupId = balances.firstOrNull()?.groupId ?: return

        transaction {
            BalanceTable.deleteWhere { BalanceTable.groupId eq groupId }

            balances.forEach { balance ->
                BalanceTable.insert {
                    it[payMemberId] = balance.payMemberId
                    it[this.groupId] = balance.groupId
                    it[amount] = balance.amount
                    it[receiverMemberId] = balance.receiverMemberId
                }
            }
        }
    }

    fun deletePaidByExpense(expenseId: Int) {
        transaction {
            PaidByExpenseTable.deleteWhere { PaidByExpenseTable.expenseId eq expenseId }
        }
    }

    fun deleteMemberExpenses(expenseId: Int) {
        transaction {
            MemberExpensesTable.deleteWhere { MemberExpensesTable.expenseId eq expenseId }
        }
    }
}