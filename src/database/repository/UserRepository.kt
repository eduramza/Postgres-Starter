package com.databaseexample.database.repository

import com.databaseexample.database.DatabaseFactory.dbQuery
import com.databaseexample.database.model.User
import com.databaseexample.database.model.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class UserRepository {

    suspend fun getAllUsers() = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { toUser(it) }
            .singleOrNull()
    }

    suspend fun getUserById(userId: String): User? = dbQuery {
        Users.select{
            (Users.id eq userId)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun createNewUser(user: User) = dbQuery {
        Users.insert {
            it[id] = user.id
            it[email] = user.email
            it[password] = user.password
            it[active] = user.active
        }
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            email = row[Users.email],
            password = row[Users.password],
            active = row[Users.active]
        )
}