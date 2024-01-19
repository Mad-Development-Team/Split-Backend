package com.madteam.repository

import com.madteam.data.model.User
import com.madteam.data.table.UserTable
import com.madteam.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class UserRepository {

    suspend fun addUser(newUser: User): Boolean {
        return try {
            dbQuery {
                UserTable.insert { userTable ->
                    userTable[UserTable.name] = newUser.name
                    userTable[UserTable.email] = newUser.email
                    userTable[UserTable.createdDate] = newUser.createdDate
                    userTable[UserTable.passwordHash] = newUser.passwordHash
                    userTable[UserTable.passwordSalt] = newUser.passwordSalt
                    userTable[UserTable.profileImage] = newUser.profileImage
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUser(
        userId: Int,
        name: String?,
        profileImage: String?
    ): Boolean {
        return try {
            dbQuery {
                val updateStatement = UserTable.update({UserTable.id eq userId}) {
                    if (!name.isNullOrBlank()){
                        it[UserTable.name] = name
                    }
                    if (!profileImage.isNullOrBlank()) {
                        it[UserTable.profileImage] = profileImage
                    }
                }
                updateStatement > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeProfileImage(
        userId: Int
    ): Boolean{
        return try {
            dbQuery {
                val updateStatement = UserTable.update({UserTable.id eq userId}) {
                    it[profileImage] = ""
                }
                updateStatement > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserByEmail(email: String) = dbQuery {
        UserTable.selectAll().where { UserTable.email eq email }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    suspend fun findUserById(id: Int) = dbQuery {
        UserTable.selectAll().where { UserTable.id.eq(id) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?) : User? {
        if (row == null){
            return null
        }

        return User(
            name = row[UserTable.name],
            email = row[UserTable.email],
            passwordHash = row[UserTable.passwordHash],
            profileImage = row[UserTable.profileImage],
            createdDate = row[UserTable.createdDate],
            passwordSalt = row[UserTable.passwordSalt]
        )
    }

}