package com.example.studentorganizer.data.model

import java.util.UUID

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val deadline: String,
    val isDone: Boolean = false,
    val isImportant: Boolean = false,
    val notes: String = "",
    val priority: TaskPriority = TaskPriority.Medium,
    val repeat: TaskRepeat = TaskRepeat.None,
    val reminderEnabled: Boolean = false
)

enum class TaskPriority { Low, Medium, High }
enum class TaskRepeat { None, Daily, Weekly }

fun defaultTasks(): List<TaskItem> = listOf(
    TaskItem(
        title = "Домашка",
        subject = "Английский",
        deadline = "21 февр. 23:59"
    )
)
