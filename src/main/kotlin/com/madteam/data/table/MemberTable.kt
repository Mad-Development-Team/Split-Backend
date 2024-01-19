package com.madteam.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

private const val MAX_DATE_LENGTH = 512
private const val MAX_COLOR_LENGTH = 512
private const val MAX_NAME_LENGTH = 36
private const val MAX_PROFILE_IMAGE_LENGTH = 2097152

object MemberTable : IntIdTable(
    name = "member"
) {
    val name = varchar("name", length = MAX_NAME_LENGTH)
    val profileImage = varchar("profile_image", length = MAX_PROFILE_IMAGE_LENGTH).nullable()
    val user = reference("user", UserTable.id).nullable()
    val color = varchar("color", length = MAX_COLOR_LENGTH)
    val joinedDate = varchar("joined_date", MAX_DATE_LENGTH)
    val groupId = integer("group_id").references(GroupTable.id)
}