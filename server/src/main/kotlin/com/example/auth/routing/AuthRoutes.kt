package com.example.auth.routing

import com.example.auth.database.UserRepository
import com.example.auth.model.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.ContentType
import java.io.File

fun Route.registerAuthRoutes() {
    route("/api/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

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

            if (UserRepository.emailExists(request.email)) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("Пользователь с таким email уже существует", "email")
                )
                return@post
            }

            val user = UserRepository.createUser(
                email = request.email,
                password = request.password,
                fullName = request.fullName,
                course = request.course,
                institute = request.institute
            )

            call.respond(HttpStatusCode.Created, SuccessResponse("Регистрация успешна", user))
        }

        put("/profile") {
            val request = call.receive<UpdateProfileRequest>()

            val fullNameError = ValidationUtil.validateFullName(request.fullName)
            if (fullNameError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(fullNameError, "fullName"))
                return@put
            }

            val existing = UserRepository.findById(request.userId)
            if (existing == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Пользователь не найден"))
                return@put
            }

            val updated = UserRepository.updateProfile(
                userId = request.userId,
                fullName = request.fullName,
                course = request.course,
                institute = request.institute
            )
            if (updated != null) {
                call.respond(HttpStatusCode.OK, SuccessResponse("Профиль обновлён", updated))
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Ошибка обновления профиля"))
            }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val emailError = ValidationUtil.validateEmail(request.email)
            if (emailError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(emailError, "email"))
                return@post
            }

            if (request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Пароль обязателен", "password"))
                return@post
            }

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

fun Route.registerAvatarRoutes() {
    route("/api/profile") {
        post("/avatar") {
            val multipart = call.receiveMultipart()
            var userId: Int? = null
            var fileData: ByteArray? = null
            var fileName: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "userId") {
                            userId = part.value.toIntOrNull()
                        }
                        part.dispose()
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName ?: "avatar.jpg"
                        val maxSize = 5 * 1024 * 1024
                        val bytes = part.streamProvider().readBytes()
                        if (bytes.size > maxSize) {
                            call.respond(HttpStatusCode.PayloadTooLarge, ErrorResponse("Файл слишком большой (макс. 5MB)"))
                            return@forEachPart
                        }
                        fileData = bytes
                        part.dispose()
                    }
                    else -> {
                        part.dispose()
                    }
                }
            }

            if (userId == null || fileData == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Требуется userId и файл"))
                return@post
            }

            val allowedExtensions = listOf("jpg", "jpeg", "png", "webp")
            val ext = fileName!!.substringAfterLast(".", "").lowercase()
            if (ext !in allowedExtensions) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Поддерживаются только JPG, PNG, WEBP"))
                return@post
            }

            val avatarsDir = File("avatars")
            if (!avatarsDir.exists()) avatarsDir.mkdirs()

            val uniqueFileName = "${userId}_${System.currentTimeMillis()}.$ext"
            val file = File(avatarsDir, uniqueFileName)
            file.writeBytes(fileData!!)

            val oldFilename = UserRepository.getAvatarFilename(userId!!)
            if (oldFilename != null) {
                File(avatarsDir, java.io.File(oldFilename).name).delete()
            }

            val avatarUrl = UserRepository.updateAvatar(userId!!, uniqueFileName)
            if (avatarUrl != null) {
                val updatedUser = UserRepository.findById(userId!!)
                call.respond(HttpStatusCode.OK, SuccessResponse("Аватарка загружена", updatedUser))
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Ошибка при сохранении"))
            }
        }
    }

    route("/api/avatars/{filename}") {
        get {
            val filename = call.parameters["filename"] ?: return@get
            val file = File("avatars", filename)
            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Файл не найден"))
                return@get
            }
            call.respond(file.readBytes())
        }
    }
}

fun Route.registerUniversityRoutes() {
    route("/api/universities") {
        get {
            val jsonFile = File("universities.json")
            if (!jsonFile.exists()) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Файл ВУЗов не найден"))
                return@get
            }

            val content = jsonFile.readText()
            call.respondText(content, ContentType.Application.Json)
        }

        get("/search") {
            val query = call.request.queryParameters["q"]?.lowercase() ?: ""
            if (query.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Укажите поисковый запрос"))
                return@get
            }

            val jsonFile = File("universities.json")
            if (!jsonFile.exists()) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Файл ВУЗов не найден"))
                return@get
            }

            val jsonArray = org.json.JSONArray(jsonFile.readText())
            val filtered = org.json.JSONArray()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val name = obj.optString("name", "").lowercase()
                val city = obj.optString("city", "").lowercase()
                val type = obj.optString("type", "").lowercase()

                if (name.contains(query) || city.contains(query) || type.contains(query)) {
                    filtered.put(obj)
                    if (filtered.length() >= 50) break
                }
            }

            call.respondText(filtered.toString(), ContentType.Application.Json)
        }
    }
}
