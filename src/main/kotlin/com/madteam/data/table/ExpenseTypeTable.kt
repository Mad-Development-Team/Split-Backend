package com.madteam.data.table

import org.jetbrains.exposed.dao.id.IdTable


object ExpenseTypeTable: IdTable<Int>(name = "expense_type") {

    val tableId = integer("id").entityId()
    val title = varchar("title", length = 512)
    val icon = varchar("icon", length = 512)
    val group = integer("group").nullable()

    override val id = tableId
}