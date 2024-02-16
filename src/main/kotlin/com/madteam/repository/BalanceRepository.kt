package com.madteam.repository

import com.madteam.data.model.Balance
import com.madteam.data.model.Expense
import com.madteam.data.table.BalanceTable
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class BalanceRepository {

    private val client = HttpClient()

    @OptIn(InternalAPI::class)
    suspend fun getBalances(
        groupExpenses: List<Expense>,
    ): List<Balance> {

        if (groupExpenses.isEmpty()) throw Exception("No expenses provided")

        val requestBody = convertExpensesToRequestFormat(groupExpenses)

        val response: HttpResponse = client.post("https://split-balance-algorithm.vercel.app/balance") {
            contentType(ContentType.Application.Json)
            body = requestBody
        }
        val groupId = groupExpenses.first().groupId

        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.bodyAsText()
            val balanceResponse = Json.decodeFromString<Map<String, List<List<JsonElement>>>>(responseBody)
            return balanceResponse["balance"]!!.map { balanceItem ->
                Balance(
                    id = 0,
                    groupId = groupId,
                    payMemberId = balanceItem[2].jsonPrimitive.content.toInt(),
                    receiverMemberId = balanceItem[0].jsonPrimitive.content.toInt(),
                    amount = balanceItem[1].jsonPrimitive.double
                )
            }
        } else {
            throw Exception("Failed to fetch balances: ${response.status.description}")
        }
    }

    private fun convertExpensesToRequestFormat(expenses: List<Expense>): String {
        val formattedExpenses = mutableListOf<List<String>>()

        expenses.forEach { expense ->
            expense.paidBy.forEach { paidBy ->
                val amountPerPayer = paidBy.amount
                val forWhomIds = expense.forWhom.map { it.memberId.toString() }
                val expenseEntry = mutableListOf(paidBy.memberId.toString(), amountPerPayer.toString()) + forWhomIds
                formattedExpenses.add(expenseEntry)
            }
        }

        val requestBody = mapOf("expenses" to formattedExpenses)

        return Json.encodeToString(requestBody)
    }

    fun getGroupBalances(groupId: Int): List<Balance> {
        return transaction {
            BalanceTable.selectAll().where { BalanceTable.groupId eq groupId }
                .mapNotNull { toBalance(it) }
        }
    }

    fun toBalance(row: ResultRow): Balance =
        Balance(
            id = row[BalanceTable.id].value,
            groupId = row[BalanceTable.groupId],
            payMemberId = row[BalanceTable.payMemberId],
            receiverMemberId = row[BalanceTable.receiverMemberId],
            amount = row[BalanceTable.amount]
        )
}