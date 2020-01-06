package com.databaseexample.database.model

import io.ktor.auth.Principal
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

data class User(
    val id: String,
    val email: String,
    val password: String,
    val active: Boolean
): Principal

object Users: Table() {
    val id: Column<String> = varchar("user_id", 200).primaryKey()
    val email: Column<String> = varchar("email", 80).uniqueIndex()
    val password: Column<String> = varchar("password", 64)
    val active: Column<Boolean> = bool("active")
}