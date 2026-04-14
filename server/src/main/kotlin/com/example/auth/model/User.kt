package com.example.auth.model

import kotlinx.serialization.Serializable

// Запрос регистрации
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val course: String? = null,
    val institute: String? = null
)

// Запрос входа
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// Ответ с данными пользователя
@Serializable
data class UserResponse(
    val id: Int,
    val email: String,
    val fullName: String,
    val course: String?,
    val institute: String?
)

// Ответ с ошибкой
@Serializable
data class ErrorResponse(
    val message: String,
    val field: String? = null
)

// Успешный ответ
@Serializable
data class SuccessResponse(
    val message: String,
    val user: UserResponse? = null
)
