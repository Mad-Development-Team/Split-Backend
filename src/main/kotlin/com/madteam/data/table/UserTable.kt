package com.madteam.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

private const val MAX_NAME_LENGTH = 36
private const val MAX_EMAIL_LENGTH = 512
private const val MAX_HASH_PASSWORD_LENGTH = 512
private const val MAX_PROFILE_IMAGE_LENGTH = 2097152
private const val MAX_DATE_LENGTH = 512

object UserTable : Table() {

    val id = integer("id")
    val email = varchar("email", length = MAX_EMAIL_LENGTH)
    val name = varchar("name", length = MAX_NAME_LENGTH)
    val profileImage = varchar("profile_image", length = MAX_PROFILE_IMAGE_LENGTH)
    val createdDate = varchar("created_date", MAX_DATE_LENGTH)
    val passwordHash = varchar("password_hash", length = MAX_HASH_PASSWORD_LENGTH)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)

}