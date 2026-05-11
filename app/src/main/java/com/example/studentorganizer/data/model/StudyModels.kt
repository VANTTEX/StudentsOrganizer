package com.example.studentorganizer.data.model

import java.util.UUID

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val deadline: String,
    val done: Boolean = false,
    val priority: String = "Средний",
    val repeatMode: String = "Нет"
)

data class ScheduleItem(
    val id: String = UUID.randomUUID().toString(),
    val weekType: String,
    val time: String,
    val subject: String,
    val place: String
)

data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis()
)

fun defaultTasks(): List<TaskItem> = listOf(
    TaskItem(title = "Сдать лабу по ОПП", subject = "ОПП", deadline = "Сегодня 18:00"),
    TaskItem(title = "Повторить БД", subject = "БД", deadline = "Завтра 10:00", done = true)
)

fun defaultSchedule(): List<ScheduleItem> = listOf(
    ScheduleItem(weekType = "Верхняя", time = "09:50", subject = "ОПП", place = "Ауд. 7.11"),
    ScheduleItem(weekType = "Верхняя", time = "11:25", subject = "Практика решения задач", place = "Ауд. 6.6"),
    ScheduleItem(weekType = "Нижняя", time = "13:45", subject = "Генератороведение", place = "Лаб. 632")
)

fun defaultNotes(): List<NoteItem> = listOf(
    NoteItem(title = "Конспект по ОПП", content = "Инкапсуляция, наследование, полиморфизм"),
    NoteItem(title = "План на неделю", content = "Лаба, презентация, коллоквиум")
)
