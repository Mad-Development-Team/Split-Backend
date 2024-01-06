package com.madteam.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSwagger(){
    install(SwaggerUI){
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = true
        }
        routing {
            
        }
        info {
            title = "Split back API"
            version = "latest"
            description = "Split back API in development."
        }
    }
}