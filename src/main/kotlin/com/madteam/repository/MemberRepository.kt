package com.madteam.repository

import com.madteam.data.model.Member
import com.madteam.data.table.MemberTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class MemberRepository {

    fun createMember(member: Member): Member {
        val insertedId = transaction {
            MemberTable.insertAndGetId {
                it[name] = member.name
                it[profileImage] = member.profileImage
                it[user] = member.user
                it[color] = member.color ?: ""
                it[joinedDate] = member.joinedDate
                it[groupId] = member.groupId
            }
        }
        return getMemberById(insertedId.value) ?: throw IllegalStateException("Member not found after creation")
    }

    private fun getMemberById(id: Int): Member? {
        return transaction {
            MemberTable.select { MemberTable.id eq id }
                .mapNotNull { toMember(it) }
                .singleOrNull()
        }
    }

    private fun toMember(row: ResultRow): Member =
        Member(
            id = row[MemberTable.id].value,
            name = row[MemberTable.name],
            profileImage = row[MemberTable.profileImage],
            user = row[MemberTable.user]?.value,
            color = row[MemberTable.color],
            joinedDate = row[MemberTable.joinedDate],
            groupId = row[MemberTable.groupId]
        )

    fun getGroupMembers(groupId: Int): List<Member> {
        return transaction {
            MemberTable.selectAll().where { MemberTable.groupId eq groupId }
                .mapNotNull { toMember(it) }
        }
    }
}