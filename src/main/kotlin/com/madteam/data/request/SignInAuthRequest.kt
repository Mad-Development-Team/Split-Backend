package com.madteam.data.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInAuthRequest(
    val email: String,
    val password: String
)
