package com.madteam.routes

import com.madteam.data.model.User
import com.madteam.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUserInfo() {
    authenticate {
        get("getUserInfo") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)

            if (userId != null) {

                val user = UserRepository().findUserById(userId.toInt())

                if (user != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        User(
                            id = user.id,
                            name = user.name,
                            email = user.email,
                            passwordHash = "",
                            profileImage = user.profileImage ?: "",
                            createdDate = user.createdDate,
                            passwordSalt = ""
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            }
        }
    }
}