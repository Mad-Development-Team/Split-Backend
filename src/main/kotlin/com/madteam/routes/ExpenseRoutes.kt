package com.madteam.routes

import com.madteam.data.model.Expense
import com.madteam.repository.BalanceRepository
import com.madteam.repository.ExpenseRepository
import com.madteam.repository.RealtimeRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createNewExpense(){
    val expenseRepository = ExpenseRepository()
    val balanceRepository = BalanceRepository()
    val realtimeRepository = RealtimeRepository()

    authenticate {
        post("createNewExpense"){

            var newExpenseTypeCreated = 0
            val mergedExpensesList = mutableListOf<Expense>()

            var newExpense: Expense = try {
                call.receive()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "${e.message} ${e.localizedMessage}")
                return@post
            }

            if (newExpense.expenseType.id == 0) {
                newExpenseTypeCreated = try {
                    expenseRepository.createExpenseType(newExpense.expenseType)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error creating expense type")
                    return@post
                }
            }

            if (newExpenseTypeCreated != 0) {
                 newExpense = newExpense.copy(expenseType = newExpense.expenseType.copy(id = newExpenseTypeCreated))
            }

            val createdExpense = try {
                expenseRepository.createExpense(newExpense)
            } catch (e: Exception) {
                if (newExpenseTypeCreated != 0) {
                    try {
                        expenseRepository.deleteExpenseType(newExpenseTypeCreated)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Error deleting expense type")
                        return@post
                    }
                }
                call.respond(HttpStatusCode.InternalServerError, "Error creating expense")
                return@post
            }

            val groupExpenses = try {
                expenseRepository.getGroupExpenses(createdExpense.groupId)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error obtaining expense")
                return@post
            }

            mergedExpensesList.addAll(groupExpenses)

            try {
                val response = balanceRepository.getBalances(mergedExpensesList)
                expenseRepository.updateBalances(response)
                realtimeRepository.updateGroupRealtime(createdExpense.groupId)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                try {
                    print(e.message)
                    expenseRepository.deletePaidByExpense(createdExpense.id)
                    expenseRepository.deleteMemberExpenses(createdExpense.id)
                    expenseRepository.deleteExpense(createdExpense.id)
                    if (newExpenseTypeCreated != 0) {
                        expenseRepository.deleteExpenseType(newExpenseTypeCreated)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error deleting expense")
                    return@post
                }
                call.respond(HttpStatusCode.InternalServerError, "Error calculating balance")
                return@post
            }
        }
    }
}

fun Route.editExpense(){
    val expenseRepository = ExpenseRepository()
    val balanceRepository = BalanceRepository()
    val realtimeRepository = RealtimeRepository()

    authenticate {
        post("editExpense"){

            var newExpenseTypeCreated = 0

            var requestExpense: Expense = try {
                call.receive()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "${e.message} ${e.localizedMessage}")
                return@post
            }

            if (requestExpense.expenseType.id == 0) {
                newExpenseTypeCreated = try {
                    expenseRepository.createExpenseType(requestExpense.expenseType)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error creating expense type")
                    return@post
                }
            }

            if (newExpenseTypeCreated != 0) {
                requestExpense = requestExpense.copy(expenseType = requestExpense.expenseType.copy(id = newExpenseTypeCreated))
            }

            try {
                expenseRepository.updateExpense(requestExpense)
            } catch (e: Exception) {
                if (newExpenseTypeCreated != 0) {
                    try {
                        expenseRepository.deleteExpenseType(newExpenseTypeCreated)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Error deleting expense type")
                        return@post
                    }
                }
                call.respond(HttpStatusCode.InternalServerError, "Error updating expense")
                return@post
            }

            val groupExpenses = try {
                expenseRepository.getGroupExpenses(requestExpense.groupId)
            } catch (e: Exception) {

                call.respond(HttpStatusCode.InternalServerError, "Error obtaining expense")
                return@post
            }

            try {
                val response = balanceRepository.getBalances(groupExpenses)
                expenseRepository.updateBalances(response)
                realtimeRepository.updateGroupRealtime(requestExpense.groupId)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                try {
                    print(e.message)
                    expenseRepository.deletePaidByExpense(requestExpense.id)
                    expenseRepository.deleteMemberExpenses(requestExpense.id)
                    expenseRepository.deleteExpense(requestExpense.id)
                    if (newExpenseTypeCreated != 0) {
                        expenseRepository.deleteExpenseType(newExpenseTypeCreated)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error deleting expense")
                    return@post
                }
                call.respond(HttpStatusCode.InternalServerError, "Error calculating balance")
                return@post
            }
        }
    }
}

fun Route.deleteExpense() {
    val expenseRepository = ExpenseRepository()
    val balanceRepository = BalanceRepository()
    val realtimeRepository = RealtimeRepository()

    authenticate {
        get("deleteExpense") {
            val groupId = call.request.queryParameters["groupId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or malformed groupId")

            val expenseId = call.request.queryParameters["expenseId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or malformed expenseId")

            val groupExpenses = try {
                expenseRepository.getGroupExpenses(groupId).toMutableList()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error obtaining group expenses")
                return@get
            }

            val expenseToDelete = groupExpenses.find { it.id == expenseId }
                ?: return@get call.respond(HttpStatusCode.NotFound, "Expense not found")

            groupExpenses.remove(expenseToDelete)

            try {
                val response = balanceRepository.getBalances(groupExpenses)
                try {
                    expenseRepository.deleteExpense(expenseId)
                    expenseRepository.updateBalances(response)
                    realtimeRepository.updateGroupRealtime(groupId)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error deleting expense")
                    return@get
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error updating balances")
                return@get
            }
        }
    }
}

fun Route.getGroupExpenses(){
    val expenseRepository = ExpenseRepository()

    authenticate {
        get("getGroupExpenses"){
            val groupId = call.request.queryParameters["groupId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or malformed groupId")

            val groupExpenses = try {
                expenseRepository.getGroupExpenses(groupId)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error obtaining group expenses")
                return@get
            }

            call.respond(HttpStatusCode.OK, groupExpenses)
        }
    }
}