package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val id: Int? = null,
    val name: String,
    val profileImage: String? = null,
    val user: Int? = null,
    val color: String? = null,
    val joinedDate: String,
    val groupId: Int
)
