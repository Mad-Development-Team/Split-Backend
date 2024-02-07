package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MemberExpenses(
    val expenseId: Int,
    val memberId: Int,
    val amount: Double
)
