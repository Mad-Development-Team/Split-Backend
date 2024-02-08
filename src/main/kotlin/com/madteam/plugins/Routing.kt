package com.madteam.plugins

import com.madteam.routes.*
import com.madteam.security.hashing.HashingService
import com.madteam.security.token.TokenConfig
import com.madteam.security.token.TokenService
import io.ktor.server.application.*
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
        getUserInfo()
        getSecretInfo()
        updateUserInfo()
        removeProfileImage()
        createNewGroup()
        getUserGroups()
        getGroupExpenseTypes()
        getCurrencies()
        createNewExpense()
        getGroupExpenses()
    }
}
