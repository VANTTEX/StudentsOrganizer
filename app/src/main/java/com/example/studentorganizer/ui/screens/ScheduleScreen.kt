package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.data.model.ScheduleLesson
import com.example.studentorganizer.data.model.WeekType
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

@Composable
fun ScheduleScreen(
    lessons: List<ScheduleLesson>,
    onSubjectClick: (String) -> Unit,
    onSync: () -> Unit,
    onAdd: () -> Unit,
    onUpdateLesson: (ScheduleLesson, ScheduleLesson) -> Unit,
    onDeleteLesson: (ScheduleLesson) -> Unit
) {
    var selectedWeek by remember { mutableStateOf(WeekType.Upper) }
    var editingLesson by remember { mutableStateOf<ScheduleLesson?>(null) }
    var deletingLesson by remember { mutableStateOf<ScheduleLesson?>(null) }
    val filtered = lessons.filter { it.weekType == selectedWeek }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расписание") },
                actions = {
                    IconButton(onClick = onSync) {
                        Icon(Icons.Default.Sync, contentDescription = "Синхронизация")
                    }
                    IconButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = LightBlue
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        shape = RoundedCornerShape(10.dp),
                        selected = selectedWeek == WeekType.Upper,
                        onClick = { selectedWeek = WeekType.Upper }
                    ) { Text("Верхняя") }
                    SegmentedButton(
                        shape = RoundedCornerShape(10.dp),
                        selected = selectedWeek == WeekType.Lower,
                        onClick = { selectedWeek = WeekType.Lower }
                    ) { Text("Нижняя") }
                }
            }
            item {
                Text(
                    text = "Локальное расписание • синхронизация доступна",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            val grouped = filtered.groupBy { it.dayTitle }
            grouped.forEach { (day, dayLessons) ->
                item {
                    Text(
                        text = day,
                        color = DeepBlue,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                items(dayLessons) { lesson ->
                    LessonRow(
                        lesson = lesson,
                        onSubjectClick = { onSubjectClick(lesson.subject) },
                        onEdit = { editingLesson = lesson },
                        onDelete = { deletingLesson = lesson }
                    )
                }
            }
        }
    }

    if (editingLesson != null) {
        EditLessonDialog(
            lesson = editingLesson!!,
            onDismiss = { editingLesson = null },
            onSave = { updated ->
                onUpdateLesson(editingLesson!!, updated)
                editingLesson = null
            }
        )
    }

    if (deletingLesson != null) {
        AlertDialog(
            onDismissRequest = { deletingLesson = null },
            title = { Text("Удалить пару?") },
            text = { Text("Пара \"${deletingLesson!!.subject}\" будет удалена из локального расписания.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteLesson(deletingLesson!!)
                        deletingLesson = null
                    }
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { deletingLesson = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun LessonRow(
    lesson: ScheduleLesson,
    onSubjectClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(56.dp)) {
                Text(lesson.startTime, style = MaterialTheme.typography.bodySmall)
                Text(lesson.endTime, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(lesson.accentColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.subject,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSubjectClick() }
                )
                Text(
                    text = lesson.place,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

@Composable
private fun EditLessonDialog(
    lesson: ScheduleLesson,
    onDismiss: () -> Unit,
    onSave: (ScheduleLesson) -> Unit
) {
    var subject by remember(lesson) { mutableStateOf(lesson.subject) }
    var place by remember(lesson) { mutableStateOf(lesson.place) }
    var startTime by remember(lesson) { mutableStateOf(lesson.startTime) }
    var endTime by remember(lesson) { mutableStateOf(lesson.endTime) }
    var dayTitle by remember(lesson) { mutableStateOf(lesson.dayTitle) }
    var weekType by remember(lesson) { mutableStateOf(lesson.weekType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать пару") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Предмет") })
                OutlinedTextField(value = place, onValueChange = { place = it }, label = { Text("Место") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Начало") }
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Конец") }
                    )
                }
                OutlinedTextField(value = dayTitle, onValueChange = { dayTitle = it }, label = { Text("Дата/день") })
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        shape = RoundedCornerShape(10.dp),
                        selected = weekType == WeekType.Upper,
                        onClick = { weekType = WeekType.Upper }
                    ) { Text("Верхняя") }
                    SegmentedButton(
                        shape = RoundedCornerShape(10.dp),
                        selected = weekType == WeekType.Lower,
                        onClick = { weekType = WeekType.Lower }
                    ) { Text("Нижняя") }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        lesson.copy(
                            subject = subject,
                            place = place,
                            startTime = startTime,
                            endTime = endTime,
                            dayTitle = dayTitle,
                            weekType = weekType
                        )
                    )
                }
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
