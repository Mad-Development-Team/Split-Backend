package com.madteam.plugins

import com.madteam.controllers.handleGetUserById
import com.madteam.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/users/{userId}") {
            handleGetUserById(call)
        }
    }
}
