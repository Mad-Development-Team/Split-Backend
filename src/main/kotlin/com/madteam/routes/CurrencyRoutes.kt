package com.madteam.routes

import com.madteam.repository.CurrencyRepository
import com.madteam.routes.authenticate
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getCurrencies(){
    val currencyRepository = CurrencyRepository()
    authenticate {
        get("getCurrencies"){
            val response = currencyRepository.getCurrencies()
            call.respond(response)
        }
    }
}