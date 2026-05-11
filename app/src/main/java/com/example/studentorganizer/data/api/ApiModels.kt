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
    @SerializedName("friendId") val friendId: String,
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

data class AddFriendRequest(
    @SerializedName("friendId") val friendId: String
)

data class FriendDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("friendId") val friendId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("course") val course: String?,
    @SerializedName("institute") val institute: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("status") val status: String
)
