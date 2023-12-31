package com.madteam.data.model

import java.sql.Date

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHash: String,
    val profileImage: String,
    val createdDate: String
)
