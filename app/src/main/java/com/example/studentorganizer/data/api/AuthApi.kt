package com.example.studentorganizer.data.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ServerResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ServerResponse>

    // Аватарки
    @Multipart
    @POST("api/profile/avatar")
    suspend fun uploadAvatar(
        @Part userId: okhttp3.MultipartBody.Part,
        @Part avatar: MultipartBody.Part
    ): Response<ServerResponse>

    // ВУЗы
    @GET("api/universities")
    suspend fun getUniversities(): Response<List<UniversityDto>>

    @GET("api/universities/search")
    suspend fun searchUniversities(@Query("q") query: String): Response<List<UniversityDto>>
}
