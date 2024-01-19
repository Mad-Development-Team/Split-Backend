package com.madteam.data.table

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

private const val MAX_GROUP_NAME_LENGTH = 25
private const val MAX_GROUP_DESCRIPTION_LENGTH = 25
private const val MAX_DATE_LENGTH = 512
private const val MAX_INVITE_CODE_LENGTH = 7
private const val MAX_PROFILE_IMAGE_LENGTH = 2097152

object GroupTable : IntIdTable(
    name = "group"
) {
    val groupName = varchar("group_name", length = MAX_GROUP_NAME_LENGTH)
    val groupDescription = varchar("group_description", length = MAX_GROUP_DESCRIPTION_LENGTH).nullable()
    val inviteCode = varchar("invite_code", length = MAX_INVITE_CODE_LENGTH)
    val image = varchar("image", length = MAX_PROFILE_IMAGE_LENGTH).nullable()
    val bannerImage = varchar("banner_image", length = MAX_PROFILE_IMAGE_LENGTH).nullable()
    val createdDate = varchar("created_date", MAX_DATE_LENGTH)
}