package com.example.auth.routing

import com.example.auth.model.*
import com.example.auth.database.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAuthRoutes() {
    route("/api/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            
            // Валидация
            val emailError = ValidationUtil.validateEmail(request.email)
            if (emailError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(emailError, "email"))
                return@post
            }
            
            val passwordError = ValidationUtil.validatePassword(request.password)
            if (passwordError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(passwordError, "password"))
                return@post
            }
            
            val fullNameError = ValidationUtil.validateFullName(request.fullName)
            if (fullNameError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(fullNameError, "fullName"))
                return@post
            }
            
            // Проверка уникальности email
            if (UserRepository.emailExists(request.email)) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("Пользователь с таким email уже существует", "email")
                )
                return@post
            }
            
            // Создание пользователя
            val user = UserRepository.createUser(
                email = request.email,
                password = request.password,
                fullName = request.fullName,
                course = request.course,
                institute = request.institute
            )
            
            call.respond(HttpStatusCode.Created, SuccessResponse("Регистрация успешна", user))
        }
        
        post("/login") {
            val request = call.receive<LoginRequest>()
            
            // Валидация
            val emailError = ValidationUtil.validateEmail(request.email)
            if (emailError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(emailError, "email"))
                return@post
            }
            
            if (request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Пароль обязателен", "password"))
                return@post
            }
            
            // Проверка credentials
            val user = UserRepository.verifyPassword(request.email, request.password)
            if (user == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Неверный email или пароль")
                )
                return@post
            }
            
            call.respond(SuccessResponse("Вход выполнен", user))
        }
    }
}
