package com.databaseexample

import com.databaseexample.auth.JWTService
import com.databaseexample.auth.hashPassword
import com.databaseexample.auth.usernameValid
import com.databaseexample.database.DatabaseFactory
import com.databaseexample.database.model.LoginRegister
import com.databaseexample.database.model.User
import com.databaseexample.database.repository.UserRepository
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.request.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.response.respond
import io.ktor.routing.get

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val jwtService = JWTService()
    val dbUser = UserRepository()
    DatabaseFactory.init()

    install(ContentNegotiation){
        jackson { enable(SerializationFeature.INDENT_OUTPUT) }
    }
    install(Authentication){
        jwt("jwt") {
            verifier(jwtService.verifier)

            realm = "database stater app"
            validate{
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asString()
                val user = dbUser.getUserById(claimString)
                user
            }
        }
    }

    routing {
        post("/login"){
            val post = call.receive<LoginRegister>()

            val user = dbUser.getUserByEmail(post.email)
            if (user == null || user.password != hashPassword(post.password)){
                error("Invalid Username or Password: \n password recuperado = ${hashPassword(user!!.password)}" +
                        "\n password inserido: ${hashPassword(post.password)}")
            }

            call.respond(mapOf("token" to jwtService.generateToken(user)))
        }

        post("/signup"){
            val post = call.receive<User>()
            val hash = hashPassword(post.password)
            val newUser = User(id = post.email, email = post.email, password = hash, active = post.active)

            dbUser.createNewUser(newUser)
            call.respond("User create with Success!")
        }

        get("/home"){
            call.respond("hello Ktor!")
        }

        authenticate("jwt") {
            get("/user"){
                call.respond(dbUser.getAllUsers())
            }
        }
    }

}

