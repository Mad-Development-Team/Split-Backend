package com.madteam.repository

import com.madteam.data.model.Currency
import com.madteam.data.table.CurrencyTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CurrencyRepository {

    fun getCurrencies(): List<Currency> {
        return transaction {
            CurrencyTable.selectAll().map { toCurrency(it) }
        }
    }

    fun getCurrencyByCode(currencyCode: String): Currency? {
        return transaction {
            CurrencyTable.selectAll().where { CurrencyTable.currency eq currencyCode }
                .mapNotNull { toCurrency(it) }
                .singleOrNull()
        }
    }

    private fun toCurrency(row: ResultRow): Currency =
        Currency(
            currency = row[CurrencyTable.currency].value,
            name = row[CurrencyTable.name],
            symbol = row[CurrencyTable.symbol]
        )
}