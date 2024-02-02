package com.madteam.repository

import com.madteam.data.model.Group
import com.madteam.data.table.GroupTable
import com.madteam.data.table.MemberTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class GroupRepository {

    fun createGroup(group: Group): Group {
        val insertedId = transaction {
            GroupTable.insertAndGetId {
                it[groupName] = group.groupName
                it[groupDescription] = group.groupDescription
                it[inviteCode] = group.inviteCode ?: ""
                it[image] = group.image
                it[bannerImage] = group.bannerImage
                it[createdDate] = group.createdDate
            }
        }
        return getGroupById(insertedId.value) ?: throw IllegalStateException("Group not found after creation")
    }

    fun getGroupById(id: Int): Group? {
        return transaction {
            GroupTable.select { GroupTable.id eq id }
                .mapNotNull { toGroup(it) }
                .singleOrNull()
        }
    }

    private fun toGroup(row: ResultRow): Group =
        Group(
            id = row[GroupTable.id].value,
            groupName = row[GroupTable.groupName],
            groupDescription = row[GroupTable.groupDescription],
            inviteCode = row[GroupTable.inviteCode],
            image = row[GroupTable.image],
            bannerImage = row[GroupTable.bannerImage],
            createdDate = row[GroupTable.createdDate],
            currency = row[GroupTable.currency]
        )

    fun isInviteCodeUnique(inviteCode: String): Boolean {
        return transaction {
            GroupTable.selectAll().where { GroupTable.inviteCode eq inviteCode }
                .empty()
        }
    }

    fun getUserGroups(userId: Int): List<Group> {
        return transaction {
            GroupTable.selectAll().where { GroupTable.id inList MemberTable.selectAll()
                .where { MemberTable.user eq userId }.map { it[MemberTable.groupId] } }
                .mapNotNull { toGroup(it) }
        }
    }


}
