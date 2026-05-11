package com.example.auth

import com.example.auth.database.DatabaseFactory
import com.example.auth.routing.registerAuthRoutes
import com.example.auth.routing.registerAvatarRoutes
import com.example.auth.routing.registerFriendRoutes
import com.example.auth.routing.registerUniversityRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Инициализация БД
    DatabaseFactory.init()

    // Логирование
    install(CallLogging)

    // Сериализация JSON
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Роуты
    routing {
        registerAuthRoutes()
        registerAvatarRoutes()
        registerFriendRoutes()
        registerUniversityRoutes()

        // Health check
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
    }
}
