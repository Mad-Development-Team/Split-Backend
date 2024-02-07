package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: Int,
    val expenseTitle: String,
    val expenseDescription: String? = null,
    val totalAmount: Double,
    val expenseType: ExpenseType,
    val createdDate: String,
    val groupId: Int,
    val currency: Currency,
    val paymentMethod: String?,
    val images: List<String>? = null,
    val paidBy: List<PaidBy>,
    val forWhom: List<MemberExpenses>
)
