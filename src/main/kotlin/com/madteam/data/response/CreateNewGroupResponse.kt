package com.madteam.data.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateNewGroupResponse(
    val id: Int,
    val groupName: String,
    val groupDescription: String? = null,
    val createdDate: String,
    val inviteCode: String,
    val image: String? = null,
    val bannerImage: String? = null,
    val currency: String
)
