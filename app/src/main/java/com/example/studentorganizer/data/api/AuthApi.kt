package com.example.studentorganizer.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ServerResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ServerResponse>
}
