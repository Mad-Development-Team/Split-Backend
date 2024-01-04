package com.madteam.data.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpAuthRequest(
    val email: String,
    val password: String,
    val name: String
)
