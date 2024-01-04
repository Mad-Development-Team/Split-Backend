package com.madteam

import com.madteam.plugins.*
import com.madteam.repository.DatabaseFactory
import com.madteam.security.hashing.SHA256HashingService
import com.madteam.security.token.JwtTokenService
import com.madteam.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    DatabaseFactory.init()
    configureRouting()
    configureSecurity(tokenConfig)
}
