package com.madteam.plugins

import com.madteam.controllers.handleGetUserById
import com.madteam.repository.UserRepository
import com.madteam.routes.authenticate
import com.madteam.routes.getSecretInfo
import com.madteam.routes.signIn
import com.madteam.routes.signUp
import com.madteam.security.hashing.HashingService
import com.madteam.security.token.TokenConfig
import com.madteam.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )
        signUp(
            hashingService = hashingService
        )
        authenticate()
        getSecretInfo()
    }
}
