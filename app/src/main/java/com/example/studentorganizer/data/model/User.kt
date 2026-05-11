package com.example.studentorganizer.data.model

data class User(
    val id: Int = 0,
    val friendId: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val faculty: String = "",
    val course: String = "",
    val university: String = "",
    val avatarUrl: String = ""
) {
    fun courseDisplay(): String {
        return if (course.isNotEmpty()) "Курс: $course" else "Курс не указан"
    }
}
