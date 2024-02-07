package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaidBy(
    val expenseId: Int,
    val memberId: Int,
    val amount: Double
)
