package com.madteam.data.model

import java.sql.Date

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val passwordHash: String,
    val profileImage: String? = null,
    val createdDate: String,
    val passwordSalt: String
)
