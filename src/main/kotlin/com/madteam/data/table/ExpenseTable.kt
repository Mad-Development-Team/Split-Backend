package com.madteam.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object ExpenseTable : IntIdTable(
    name = "expense"
) {
    val expenseTitle = varchar("expense_title", length = 50)
    val expenseDescription = varchar("expense_description", length = 500).nullable()
    val totalAmount = double("total_amount")
    val expenseType = integer("expense_type")
    val createdDate = varchar("created_date", 250)
    val groupId = integer("group")
    val images = varchar("images", length = 25860).nullable()
    val paymentMethod = varchar("payment_method", length = 50).nullable()
    val currency = varchar("currency", length = 50)
}