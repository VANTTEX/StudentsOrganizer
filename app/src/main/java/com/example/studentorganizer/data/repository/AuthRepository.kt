package com.example.studentorganizer.data.repository

import android.content.Context
import android.net.Uri
import com.example.studentorganizer.data.api.*
import com.example.studentorganizer.data.storage.UserPreferencesRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AuthRepository(
    private val api: AuthApi = RetrofitClient.api,
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
                val user = body.userDto
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
                val user = body.userDto
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

    suspend fun uploadAvatar(userId: Int, uri: Uri, context: Context): Result<String> {
        return try {
            // Конвертируем Uri во временный файл
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Не удалось открыть изображение"))

            val tempFile = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { out ->
                inputStream.copyTo(out)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val avatarPart = MultipartBody.Part.createFormData("avatar", tempFile.name, requestFile)
            val userIdPart = MultipartBody.Part.createFormData("userId", userId.toString())

            val response = api.uploadAvatar(userIdPart, avatarPart)

            if (response.isSuccessful) {
                // Получаем URL аватарки из ответа сервера
                val responseBody = response.body()
                val avatarUrl = responseBody?.userDto?.avatarUrl
                    ?: RetrofitClient.BASE_URL + "api/avatars/${tempFile.name}"
                prefsRepository.updateAvatarUrl(avatarUrl)
                tempFile.delete()
                Result.success(avatarUrl)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Ошибка загрузки аватарки"))
        }
    }

    suspend fun getUniversities(): Result<List<UniversityDto>> {
        return try {
            val response = api.getUniversities()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка загрузки списка ВУЗов"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Ошибка загрузки ВУЗов"))
        }
    }

    suspend fun searchUniversities(query: String): Result<List<UniversityDto>> {
        return try {
            val response = api.searchUniversities(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка поиска ВУЗов"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Ошибка поиска"))
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
