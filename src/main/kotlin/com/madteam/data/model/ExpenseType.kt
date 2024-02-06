package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseType(
    val id: Int,
    val title: String,
    val icon: String,
    val group: Int? = null
)
