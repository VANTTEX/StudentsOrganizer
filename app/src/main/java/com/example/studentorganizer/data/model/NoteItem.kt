package com.example.studentorganizer.data.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class NoteChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val checked: Boolean = false
)

data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val folder: String = "Общие",
    val backgroundColor: Color = Color(0xFFEAF3FF),
    val fontSizeSp: Float = 16f,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val checklist: List<NoteChecklistItem> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun defaultNotes(): List<NoteItem> = listOf(
    NoteItem(
        title = "Конспект по ОПП",
        content = "Полиморфизм, инкапсуляция, наследование",
        folder = "Учёба",
        backgroundColor = Color(0xFFFFF7D6),
        checklist = listOf(
            NoteChecklistItem(text = "Повторить SOLID", checked = true),
            NoteChecklistItem(text = "Сделать примеры")
        )
    )
)
