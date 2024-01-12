package com.madteam.routes

import com.madteam.data.model.User
import com.madteam.data.request.UpdateUserInfoRequest
import com.madteam.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
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

fun Route.updateUserInfo(){
    authenticate {
        post("updateUserInfo"){
            val request: UpdateUserInfoRequest
            try {
                request = call.receive()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "${e.message} ${e.localizedMessage}")
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()

            if (userId != null) {
                if (request.name.isNullOrBlank() && request.profileImage.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "No valid parameters on request")
                } else {
                    val updateResult = UserRepository().updateUser(userId, request.name, request.profileImage)

                    if (updateResult) {
                        call.respond(HttpStatusCode.OK, "User updated successfully")
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Error updating user")
                    }
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            }
        }
    }
}

fun Route.removeProfileImage(){
    authenticate {
        get("removeProfileImage"){
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()

            if (userId != null) {
                val updateResult = UserRepository().removeProfileImage(userId)

                if (updateResult) {
                    call.respond(HttpStatusCode.OK, "User updated successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error removing user profile image")
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            }
        }
    }
}