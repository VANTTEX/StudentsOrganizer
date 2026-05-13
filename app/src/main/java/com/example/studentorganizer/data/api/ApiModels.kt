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

data class UpdateProfileRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("course") val course: String?,
    @SerializedName("institute") val institute: String?
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("course") val course: String?,
    @SerializedName("institute") val institute: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class ServerResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val userDto: UserDto?,
    @SerializedName("field") val field: String?
)

data class UniversityDto(
    @SerializedName("name") val name: String,
    @SerializedName("city") val city: String,
    @SerializedName("type") val type: String,
    @SerializedName("website") val website: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: String?
)
