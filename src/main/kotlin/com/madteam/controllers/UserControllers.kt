package com.madteam.controllers

import com.madteam.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun handleGetUserById(call: ApplicationCall) {
    val userId = call.parameters["userId"]?.toIntOrNull()
    if (userId != null) {
        val user = UserRepository().findUserById(userId)
        if (user != null) {
            call.respondText(user.toString())
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    } else {
        call.respond(HttpStatusCode.BadRequest, "Invalid userId")
    }
}