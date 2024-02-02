package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val currency: String,
    val name: String,
    val symbol: String
)
