package com.example.studentorganizer.data.model

import androidx.compose.ui.graphics.Color

enum class WeekType { Upper, Lower }

data class ScheduleLesson(
    val startTime: String,
    val endTime: String,
    val subject: String,
    val place: String,
    val weekType: WeekType,
    val dayTitle: String,
    val accentColor: Color
)

fun defaultLessons(): List<ScheduleLesson> = listOf(
    ScheduleLesson("13:45", "15:20", "Имитационное Моделирование", "Лаб. 513, Петров", WeekType.Upper, "Понедельник 16 февр. 2026 г.", Color(0xFFB39DDB)),
    ScheduleLesson("15:25", "17:00", "Большие данные", "Лаб. 324, Смирнов", WeekType.Upper, "Понедельник 16 февр. 2026 г.", Color(0xFF80DEEA)),
    ScheduleLesson("11:25", "13:00", "Практика решения задач", "Ауд. 6.6, Кондратенкова", WeekType.Upper, "Среда 18 февр. 2026 г.", Color(0xFFFFCC80)),
    ScheduleLesson("13:45", "15:20", "ОПП", "Ауд. 8.4, Чуниха", WeekType.Upper, "Среда 18 февр. 2026 г.", Color(0xFF90CAF9)),
    ScheduleLesson("15:25", "17:00", "Большие данные", "Лаб. 63, Гладкоуров", WeekType.Upper, "Среда 18 февр. 2026 г.", Color(0xFF80DEEA)),
    ScheduleLesson("13:45", "15:20", "ОПП", "Ауд. 7.11, Петрич", WeekType.Lower, "Среда 25 февр. 2026 г.", Color(0xFF90CAF9)),
    ScheduleLesson("09:50", "11:25", "Практика решения задач", "Ауд. 7.11, Петрич", WeekType.Lower, "Среда 25 февр. 2026 г.", Color(0xFFFFCC80)),
    ScheduleLesson("11:25", "13:00", "Генератороведение", "Лаб. 632, Плеханов", WeekType.Lower, "Пятница 20 февр. 2026 г.", Color(0xFFA5D6A7)),
    ScheduleLesson("17:10", "18:45", "Имитационное Моделирование", "Кабинет 7001", WeekType.Lower, "Пятница 20 февр. 2026 г.", Color(0xFFB39DDB))
)
