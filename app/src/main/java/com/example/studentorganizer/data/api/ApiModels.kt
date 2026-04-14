package com.example.studentorganizer.data.api

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("course") val course: String?,
    @SerializedName("institute") val institute: String?
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("course") val course: String?,
    @SerializedName("institute") val institute: String?
)

data class ServerResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDto?,
    @SerializedName("field") val field: String?
)
