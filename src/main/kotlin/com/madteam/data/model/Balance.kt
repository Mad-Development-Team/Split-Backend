package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    val id: Int,
    val groupId: Int,
    val payMemberId: Int,
    val receiverMemberId: Int,
    val amount: Double
)
