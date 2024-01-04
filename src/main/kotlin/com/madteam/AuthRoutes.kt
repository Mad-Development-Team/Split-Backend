package com.madteam

import com.madteam.data.model.User
import com.madteam.data.request.SignUpAuthRequest
import com.madteam.datasource.UserDataSource
import com.madteam.security.hashing.HashingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Date

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
    post("signup") {
        val request = call.receiveOrNull<SignUpAuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.email.isBlank() || request.password.isBlank() || request.name.isBlank()
        val isPasswordValid = request.password.length < 8
        if (areFieldsBlank || isPasswordValid){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            id = null,
            passwordSalt = saltedHash.salt,
            passwordHash = saltedHash.hash,
            email = request.email,
            name = request.name,
            profileImage = null,
            createdDate = Date().toString()
        )
    }
}