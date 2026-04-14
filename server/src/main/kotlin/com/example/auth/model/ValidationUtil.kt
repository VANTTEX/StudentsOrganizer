package com.example.auth.model

object ValidationUtil {
    
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    
    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email обязателен"
        if (!EMAIL_REGEX.matches(email)) return "Некорректный формат email"
        return null
    }
    
    fun validatePassword(password: String): String? {
        if (password.length < 8) return "Пароль должен содержать минимум 8 символов"
        if (!password.any { it.isLetter() }) return "Пароль должен содержать хотя бы одну букву"
        if (!password.any { it.isDigit() }) return "Пароль должен содержать хотя бы одну цифру"
        return null
    }
    
    fun validateFullName(fullName: String): String? {
        if (fullName.isBlank()) return "ФИО обязательно"
        if (fullName.trim().length < 2) return "ФИО слишком короткое"
        return null
    }
}
