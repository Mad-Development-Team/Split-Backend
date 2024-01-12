package com.madteam.data.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserInfoRequest(
    val name: String?,
    val profileImage: String?
)
