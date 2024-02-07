package com.madteam.data.table

import org.jetbrains.exposed.sql.Table

object PaidByExpenseTable : Table("paid_by_expense") {
    val expenseId = integer("expense_id")
    val memberId = integer("member_id")
    val paidAmount = double("paid_amount")

    override val primaryKey = PrimaryKey(expenseId, memberId, name = "PK_PaidByExpense_Id_Member")
}