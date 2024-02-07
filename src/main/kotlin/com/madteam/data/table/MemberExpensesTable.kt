package com.madteam.data.table

import org.jetbrains.exposed.sql.Table

object MemberExpensesTable : Table("member_expenses") {
    val memberId = integer("member_id")
    val expenseId = integer("expense_id")
    val paidAmount = double("amount")

    override val primaryKey = PrimaryKey(memberId, expenseId, name = "PK_MemberExpense_Id_Expense")
}