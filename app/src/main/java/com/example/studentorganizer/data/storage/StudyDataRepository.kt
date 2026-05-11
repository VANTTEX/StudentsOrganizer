package com.example.studentorganizer.data.storage

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.studentorganizer.data.model.NoteChecklistItem
import com.example.studentorganizer.data.model.NoteItem
import com.example.studentorganizer.data.model.ScheduleLesson
import com.example.studentorganizer.data.model.TaskPriority
import com.example.studentorganizer.data.model.TaskRepeat
import com.example.studentorganizer.data.model.TaskItem
import com.example.studentorganizer.data.model.WeekType
import com.example.studentorganizer.data.model.defaultNotes
import com.example.studentorganizer.data.model.defaultLessons
import com.example.studentorganizer.data.model.defaultTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.studyDataStore by preferencesDataStore(name = "study_data")

class StudyDataRepository(private val context: Context) {

    private val tasksKey = stringPreferencesKey("tasks_json")
    private val lessonsKey = stringPreferencesKey("lessons_json")
    private val notesKey = stringPreferencesKey("notes_json")

    val tasksFlow: Flow<List<TaskItem>> = context.studyDataStore.data.map { prefs ->
        val raw = prefs[tasksKey]
        if (raw.isNullOrBlank()) defaultTasks() else decodeTasks(raw)
    }

    val lessonsFlow: Flow<List<ScheduleLesson>> = context.studyDataStore.data.map { prefs ->
        val raw = prefs[lessonsKey]
        if (raw.isNullOrBlank()) defaultLessons() else decodeLessons(raw)
    }

    val notesFlow: Flow<List<NoteItem>> = context.studyDataStore.data.map { prefs ->
        val raw = prefs[notesKey]
        if (raw.isNullOrBlank()) defaultNotes() else decodeNotes(raw)
    }

    suspend fun saveTasks(tasks: List<TaskItem>) {
        context.studyDataStore.edit { prefs ->
            prefs[tasksKey] = encodeTasks(tasks)
        }
    }

    suspend fun saveLessons(lessons: List<ScheduleLesson>) {
        context.studyDataStore.edit { prefs ->
            prefs[lessonsKey] = encodeLessons(lessons)
        }
    }

    suspend fun saveNotes(notes: List<NoteItem>) {
        context.studyDataStore.edit { prefs ->
            prefs[notesKey] = encodeNotes(notes)
        }
    }

    private fun encodeTasks(tasks: List<TaskItem>): String {
        val arr = JSONArray()
        tasks.forEach { task ->
            arr.put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title)
                    .put("subject", task.subject)
                    .put("deadline", task.deadline)
                    .put("isDone", task.isDone)
                    .put("isImportant", task.isImportant)
                    .put("notes", task.notes)
                    .put("priority", task.priority.name)
                    .put("repeat", task.repeat.name)
                    .put("reminderEnabled", task.reminderEnabled)
            )
        }
        return arr.toString()
    }

    private fun decodeTasks(raw: String): List<TaskItem> = runCatching {
        val arr = JSONArray(raw)
        buildList {
            for (index in 0 until arr.length()) {
                val obj = arr.getJSONObject(index)
                add(
                    TaskItem(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        subject = obj.optString("subject"),
                        deadline = obj.optString("deadline"),
                        isDone = obj.optBoolean("isDone"),
                        isImportant = obj.optBoolean("isImportant"),
                        notes = obj.optString("notes"),
                        priority = runCatching { TaskPriority.valueOf(obj.optString("priority")) }.getOrDefault(TaskPriority.Medium),
                        repeat = runCatching { TaskRepeat.valueOf(obj.optString("repeat")) }.getOrDefault(TaskRepeat.None),
                        reminderEnabled = obj.optBoolean("reminderEnabled", false)
                    )
                )
            }
        }
    }.getOrDefault(defaultTasks())

    private fun encodeLessons(lessons: List<ScheduleLesson>): String {
        val arr = JSONArray()
        lessons.forEach { lesson ->
            arr.put(
                JSONObject()
                    .put("startTime", lesson.startTime)
                    .put("endTime", lesson.endTime)
                    .put("subject", lesson.subject)
                    .put("place", lesson.place)
                    .put("weekType", lesson.weekType.name)
                    .put("dayTitle", lesson.dayTitle)
                    .put("accentColor", lesson.accentColor.value.toString())
            )
        }
        return arr.toString()
    }

    private fun decodeLessons(raw: String): List<ScheduleLesson> = runCatching {
        val arr = JSONArray(raw)
        buildList {
            for (index in 0 until arr.length()) {
                val obj = arr.getJSONObject(index)
                add(
                    ScheduleLesson(
                        startTime = obj.optString("startTime"),
                        endTime = obj.optString("endTime"),
                        subject = obj.optString("subject"),
                        place = obj.optString("place"),
                        weekType = runCatching { WeekType.valueOf(obj.optString("weekType")) }.getOrDefault(WeekType.Upper),
                        dayTitle = obj.optString("dayTitle"),
                        accentColor = Color(obj.optString("accentColor").toULongOrNull() ?: Color(0xFF90CAF9).value)
                    )
                )
            }
        }
    }.getOrDefault(defaultLessons())

    private fun encodeNotes(notes: List<NoteItem>): String {
        val arr = JSONArray()
        notes.forEach { note ->
            val checklistArray = JSONArray()
            note.checklist.forEach { item ->
                checklistArray.put(
                    JSONObject()
                        .put("id", item.id)
                        .put("text", item.text)
                        .put("checked", item.checked)
                )
            }
            arr.put(
                JSONObject()
                    .put("id", note.id)
                    .put("title", note.title)
                    .put("content", note.content)
                    .put("folder", note.folder)
                    .put("backgroundColor", note.backgroundColor.value.toString())
                    .put("fontSizeSp", note.fontSizeSp.toDouble())
                    .put("isBold", note.isBold)
                    .put("isItalic", note.isItalic)
                    .put("updatedAt", note.updatedAt)
                    .put("checklist", checklistArray)
            )
        }
        return arr.toString()
    }

    private fun decodeNotes(raw: String): List<NoteItem> = runCatching {
        val arr = JSONArray(raw)
        buildList {
            for (index in 0 until arr.length()) {
                val obj = arr.getJSONObject(index)
                val checklistRaw = obj.optJSONArray("checklist") ?: JSONArray()
                val checklist = buildList {
                    for (itemIndex in 0 until checklistRaw.length()) {
                        val itemObj = checklistRaw.getJSONObject(itemIndex)
                        add(
                            NoteChecklistItem(
                                id = itemObj.optString("id"),
                                text = itemObj.optString("text"),
                                checked = itemObj.optBoolean("checked")
                            )
                        )
                    }
                }
                add(
                    NoteItem(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        content = obj.optString("content"),
                        folder = obj.optString("folder", "Общие"),
                        backgroundColor = Color(obj.optString("backgroundColor").toULongOrNull() ?: Color(0xFFEAF3FF).value),
                        fontSizeSp = obj.optDouble("fontSizeSp", 16.0).toFloat(),
                        isBold = obj.optBoolean("isBold"),
                        isItalic = obj.optBoolean("isItalic"),
                        checklist = checklist,
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
        }
    }.getOrDefault(defaultNotes())
}
