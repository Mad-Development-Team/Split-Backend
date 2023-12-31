package com.madteam.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

private const val JDBC_DRIVER_ENVIRONMENT = "JDBC_DRIVER"
private const val JDBC_DB_URL_ENVIRONMENT = "JDBC_DATABASE_URL"
private const val MAXIMUM_POOL_SIZE = 3

object DatabaseFactory {

    fun init(){
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        with(config){
            driverClassName = System.getenv(JDBC_DRIVER_ENVIRONMENT)
            jdbcUrl = System.getenv(JDBC_DB_URL_ENVIRONMENT)
            maximumPoolSize = MAXIMUM_POOL_SIZE
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

}