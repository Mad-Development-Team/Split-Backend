package com.madteam.repository

import com.madteam.data.model.ExpenseType
import com.madteam.data.model.Group
import com.madteam.data.table.ExpenseTypeTable
import com.madteam.data.table.GroupTable
import com.madteam.data.table.MemberTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction


class GroupRepository {
    private val currencyRepository = CurrencyRepository()

    fun createGroup(group: Group): Group {
        val insertedId = transaction {
            GroupTable.insertAndGetId {
                it[groupName] = group.groupName
                it[groupDescription] = group.groupDescription
                it[inviteCode] = group.inviteCode
                it[image] = group.image
                it[bannerImage] = group.bannerImage
                it[createdDate] = group.createdDate
                it[currency] = group.currency.currency
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

    fun getGroupExpenseTypes(groupId: Int): List<ExpenseType> {
        return transaction {
            ExpenseTypeTable.selectAll()
                .where { (ExpenseTypeTable.group.isNull()) or (ExpenseTypeTable.group eq groupId) }.mapNotNull { toExpenseType(it) }
        }
    }

    private fun toExpenseType(row: ResultRow): ExpenseType =
        ExpenseType(
            id = row[ExpenseTypeTable.id].value,
            title = row[ExpenseTypeTable.title],
            icon = row[ExpenseTypeTable.icon],
            group = row[ExpenseTypeTable.group]
        )

    private fun toGroup(row: ResultRow): Group =
        Group(
            id = row[GroupTable.id].value,
            groupName = row[GroupTable.groupName],
            groupDescription = row[GroupTable.groupDescription],
            inviteCode = row[GroupTable.inviteCode],
            image = row[GroupTable.image],
            bannerImage = row[GroupTable.bannerImage],
            createdDate = row[GroupTable.createdDate],
            currency = currencyRepository.getCurrencyByCode(row[GroupTable.currency]) ?: throw IllegalStateException("Currency not found")
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
