package com.madteam.routes

import com.madteam.data.model.User
import com.madteam.data.request.SignInAuthRequest
import com.madteam.data.request.SignUpAuthRequest
import com.madteam.data.response.AuthResponse
import com.madteam.repository.UserRepository
import com.madteam.routes.authenticate
import com.madteam.security.hashing.HashingService
import com.madteam.security.hashing.SaltedHash
import com.madteam.security.token.TokenClaim
import com.madteam.security.token.TokenConfig
import com.madteam.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils
import java.util.Date

fun Route.signUp(
    hashingService: HashingService
){
    post("signup") {
        val request: SignUpAuthRequest
        try {
            request = call.receive()
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, "${e.message} ${e.localizedMessage}")
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
            passwordSalt = saltedHash.salt,
            passwordHash = saltedHash.hash,
            email = request.email,
            name = request.name,
            profileImage = null,
            createdDate = Date().toString()
        )

        val userExist = UserRepository().getUserByEmail(user.email)
        if (userExist != null){
            call.respond(HttpStatusCode.Conflict, "Email already registered")
            return@post
        }

        val userInserted = UserRepository().addUser(user)
        if (!userInserted){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("signin") {
        val request: SignInAuthRequest
        try {
            request = call.receive()
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, "${e.message} ${e.localizedMessage}")
            return@post
        }

        val user = UserRepository().getUserByEmail(request.email)
        if (user == null){
            call.respond(HttpStatusCode.Conflict, "User email not found")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.passwordHash,
                salt = user.passwordSalt
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Invalid username or password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate(){
    authenticate {
        get("authenticate"){
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo(){
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}