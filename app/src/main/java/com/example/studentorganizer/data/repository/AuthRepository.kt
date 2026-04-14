package com.example.studentorganizer.data.repository

import com.example.studentorganizer.data.api.LoginRequest
import com.example.studentorganizer.data.api.RegisterRequest
import com.example.studentorganizer.data.api.RetrofitClient
import com.example.studentorganizer.data.api.UserDto
import com.example.studentorganizer.data.storage.UserPreferencesRepository

class AuthRepository(
    private val api: com.example.studentorganizer.data.api.AuthApi = RetrofitClient.api,
    private val prefsRepository: UserPreferencesRepository
) {
    
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        course: String?,
        institute: String?
    ): Result<UserDto> {
        return try {
            val request = RegisterRequest(
                email = email,
                password = password,
                fullName = fullName,
                course = course,
                institute = institute
            )
            val response = api.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val user = body.user
                if (user != null) {
                    prefsRepository.saveUserFromServer(user)
                    prefsRepository.setLoggedIn(true)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorBody = response.errorBody()
                val errorMsg = parseErrorMessage(errorBody?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Нет подключения к серверу. Проверьте соединение."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Превышено время ожидания ответа от сервера"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Неизвестная ошибка"))
        }
    }
    
    suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = api.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val user = body.user
                if (user != null) {
                    prefsRepository.saveUserFromServer(user)
                    prefsRepository.setLoggedIn(true)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorBody = response.errorBody()
                val errorMsg = parseErrorMessage(errorBody?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Нет подключения к серверу. Проверьте соединение."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Превышено время ожидания ответа от сервера"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Неизвестная ошибка"))
        }
    }
    
    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody != null) {
                val json = org.json.JSONObject(errorBody)
                json.optString("message", "Произошла ошибка на сервере")
            } else {
                "Произошла ошибка на сервере"
            }
        } catch (e: Exception) {
            errorBody ?: "Произошла ошибка на сервере"
        }
    }
}
