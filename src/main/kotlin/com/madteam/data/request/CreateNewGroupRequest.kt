package com.madteam.data.request

import com.madteam.data.model.Currency
import com.madteam.data.model.Member
import kotlinx.serialization.Serializable

@Serializable
data class CreateNewGroupRequest(
    val groupName: String,
    val groupDescription: String?,
    val membersList: List<Member>,
    val currency: Currency
)
