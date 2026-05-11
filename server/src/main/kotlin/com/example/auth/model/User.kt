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
    val friendId: String,
    val email: String,
    val fullName: String,
    val course: String?,
    val institute: String?,
    val avatarUrl: String? = null
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

// Запрос добавления друга
@Serializable
data class AddFriendRequest(
    val friendId: String
)

// Ответ с данными друга
@Serializable
data class FriendResponse(
    val userId: Int,
    val friendId: String,
    val fullName: String,
    val email: String,
    val course: String?,
    val institute: String?,
    val avatarUrl: String?,
    val status: String
)
