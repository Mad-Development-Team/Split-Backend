package com.madteam.data.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object CurrencyTable : IdTable<String>(
    name = "currency"
) {
    val currency = varchar("currency", length = 512).entityId()
    val name = varchar("name", length = 512)
    val symbol = varchar("symbol", length = 512)

    override val id: Column<EntityID<String>>
        get() = currency
}