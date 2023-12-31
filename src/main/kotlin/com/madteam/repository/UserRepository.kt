package com.madteam.repository

import com.madteam.data.model.User
import com.madteam.data.table.UserTable
import com.madteam.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.DateTimeException
import java.util.Date

class UserRepository {

    suspend fun addUser(newUser: User){
        dbQuery {
            UserTable.insert {userTable ->
                userTable[UserTable.id] = newUser.id
                userTable[UserTable.name] = newUser.name
                userTable[UserTable.email] = newUser.email
                userTable[UserTable.createdDate] = newUser.createdDate
                userTable[UserTable.passwordHash] = newUser.passwordHash
                userTable[UserTable.profileImage] = newUser.profileImage
            }
        }
    }

    suspend fun findUserById(id: Int) = dbQuery {
        UserTable.select { UserTable.id.eq(id) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?) : User? {
        if (row == null){
            return null
        }

        return User(
            id = row[UserTable.id],
            name = row[UserTable.name],
            email = row[UserTable.email],
            passwordHash = row[UserTable.passwordHash],
            profileImage = row[UserTable.passwordHash],
            createdDate = row[UserTable.createdDate]
        )
    }

}