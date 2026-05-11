package com.example.studentorganizer.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.studentorganizer.data.model.NoteItem
import com.example.studentorganizer.data.model.ScheduleItem
import com.example.studentorganizer.data.model.TaskItem
import com.example.studentorganizer.data.model.defaultNotes
import com.example.studentorganizer.data.model.defaultSchedule
import com.example.studentorganizer.data.model.defaultTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.studyDataStore by preferencesDataStore(name = "study_data")

class StudyDataRepository(
    private val context: Context,
    private val accountScope: String
) {
    private val normalizedScope = accountScope.ifBlank { "guest" }
        .replace(Regex("[^a-zA-Z0-9._-]"), "_")
    private val tasksKey = stringPreferencesKey("tasks_json_$normalizedScope")
    private val scheduleKey = stringPreferencesKey("schedule_json_$normalizedScope")
    private val notesKey = stringPreferencesKey("notes_json_$normalizedScope")

    val tasksFlow: Flow<List<TaskItem>> = context.studyDataStore.data.map { prefs ->
        val raw = prefs[tasksKey]
        if (raw.isNullOrBlank()) defaultTasks() else decodeTasks(raw)
    }

    val scheduleFlow: Flow<List<ScheduleItem>> = context.studyDataStore.data.map { prefs ->
        val raw = prefs[scheduleKey]
        if (raw.isNullOrBlank()) defaultSchedule() else decodeSchedule(raw)
    }

    val notesFlow: Flow<List<NoteItem>> = context.studyDataStore.data.map { prefs ->
        val raw = prefs[notesKey]
        if (raw.isNullOrBlank()) defaultNotes() else decodeNotes(raw)
    }

    suspend fun saveTasks(items: List<TaskItem>) {
        context.studyDataStore.edit { it[tasksKey] = encodeTasks(items) }
    }

    suspend fun saveSchedule(items: List<ScheduleItem>) {
        context.studyDataStore.edit { it[scheduleKey] = encodeSchedule(items) }
    }

    suspend fun saveNotes(items: List<NoteItem>) {
        context.studyDataStore.edit { it[notesKey] = encodeNotes(items) }
    }

    private fun encodeTasks(items: List<TaskItem>): String = JSONArray().apply {
        items.forEach { task ->
            put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title)
                    .put("subject", task.subject)
                    .put("deadline", task.deadline)
                    .put("done", task.done)
                    .put("priority", task.priority)
                    .put("repeatMode", task.repeatMode)
            )
        }
    }.toString()

    private fun decodeTasks(raw: String): List<TaskItem> = runCatching {
        val arr = JSONArray(raw)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    TaskItem(
                        id = o.optString("id"),
                        title = o.optString("title"),
                        subject = o.optString("subject"),
                        deadline = o.optString("deadline"),
                        done = o.optBoolean("done"),
                        priority = o.optString("priority", "Средний"),
                        repeatMode = o.optString("repeatMode", "Нет")
                    )
                )
            }
        }
    }.getOrDefault(defaultTasks())

    private fun encodeSchedule(items: List<ScheduleItem>): String = JSONArray().apply {
        items.forEach { lesson ->
            put(
                JSONObject()
                    .put("id", lesson.id)
                    .put("weekType", lesson.weekType)
                    .put("time", lesson.time)
                    .put("subject", lesson.subject)
                    .put("place", lesson.place)
            )
        }
    }.toString()

    private fun decodeSchedule(raw: String): List<ScheduleItem> = runCatching {
        val arr = JSONArray(raw)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    ScheduleItem(
                        id = o.optString("id"),
                        weekType = o.optString("weekType"),
                        time = o.optString("time"),
                        subject = o.optString("subject"),
                        place = o.optString("place")
                    )
                )
            }
        }
    }.getOrDefault(defaultSchedule())

    private fun encodeNotes(items: List<NoteItem>): String = JSONArray().apply {
        items.forEach { note ->
            put(
                JSONObject()
                    .put("id", note.id)
                    .put("title", note.title)
                    .put("content", note.content)
                    .put("updatedAt", note.updatedAt)
            )
        }
    }.toString()

    private fun decodeNotes(raw: String): List<NoteItem> = runCatching {
        val arr = JSONArray(raw)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    NoteItem(
                        id = o.optString("id"),
                        title = o.optString("title"),
                        content = o.optString("content"),
                        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
        }
    }.getOrDefault(defaultNotes())
}
