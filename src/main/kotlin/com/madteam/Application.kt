package com.madteam

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.madteam.plugins.*
import com.madteam.repository.DatabaseFactory
import com.madteam.security.hashing.SHA256HashingService
import com.madteam.security.token.JwtTokenService
import com.madteam.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.ByteArrayInputStream
import java.util.*

private const val ONE_YEAR_IN_LONG = 365L * 24L * 60L * 60L * 1000L

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val environment = ApplicationConfig(null)

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.property("jwt.issuer").getString(),
        audience = environment.property("jwt.audience").getString(),
        expiresIn = ONE_YEAR_IN_LONG,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    initFirebaseAdmin()

    DatabaseFactory.init()
    configureSwagger()
    configureSecurity(tokenConfig)
    configureSerialization()
    configureRouting(
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )
}

fun initFirebaseAdmin() {
    val base64Encoded = System.getenv("FIREBASE_CONFIG_BASE64")
    val decodedBytes = Base64.getDecoder().decode(base64Encoded)
    val serviceAccount = ByteArrayInputStream(decodedBytes)

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)
}
