package com.madteam.repository

import com.madteam.data.model.Currency
import com.madteam.data.table.CurrencyTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CurrencyRepository {

    fun getCurrencies(): List<Currency> {
        return transaction {
            CurrencyTable.selectAll().map { toCurrency(it) }
        }
    }

    private fun toCurrency(row: ResultRow): Currency =
        Currency(
            currency = row[CurrencyTable.currency].value,
            name = row[CurrencyTable.name],
            symbol = row[CurrencyTable.symbol]
        )
}