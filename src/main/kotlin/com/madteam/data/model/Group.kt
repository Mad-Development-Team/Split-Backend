package com.madteam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: Int? = null,
    val groupName: String,
    val groupDescription: String? = null,
    val createdDate: String,
    val inviteCode: String,
    val image: String? = null,
    val bannerImage: String? = null,
    val members: List<Member>? = null,
    val currency: Currency
)
