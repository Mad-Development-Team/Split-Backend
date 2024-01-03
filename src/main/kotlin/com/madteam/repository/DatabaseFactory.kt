package com.madteam.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

private const val JDBC_DRIVER = "org.postgresql.Driver"
private const val MAXIMUM_POOL_SIZE = 3

object DatabaseFactory {

    fun init(){
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        val environment = ApplicationConfig(null)
        with(config){
            driverClassName = JDBC_DRIVER
            jdbcUrl = environment.property("storage.jdbc_url").getString()
            username = environment.property("storage.username").getString()
            password = environment.property("storage.password").getString()
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