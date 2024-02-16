package com.madteam.routes

import com.madteam.repository.BalanceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getGroupBalances(){

    val balanceRepository = BalanceRepository()

    authenticate {
        get("getGroupBalances"){
            val groupId = call.request.queryParameters["groupId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or malformed groupId")

            val groupBalances = try {
                balanceRepository.getGroupBalances(groupId)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error fetching group balances")
                return@get
            }

            call.respond(HttpStatusCode.OK, groupBalances)
        }
    }
}