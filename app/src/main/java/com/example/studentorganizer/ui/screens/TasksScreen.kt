package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.rememberDatePickerState
import com.example.studentorganizer.data.model.TaskItem
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    filterSubject: String?,
    onClearFilter: () -> Unit,
    onAdd: (TaskItem) -> Unit,
    onUpdate: (TaskItem) -> Unit,
    onDelete: (String) -> Unit
) {
    var editing by remember { mutableStateOf<TaskItem?>(null) }
    val shown = if (filterSubject.isNullOrBlank()) tasks else tasks.filter { it.subject == filterSubject }
    val doneCount = shown.count { it.done }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задания") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = TaskItem(title = "", subject = "", deadline = "") },
                containerColor = DeepBlue,
                contentColor = White
            ) { Icon(Icons.Default.Add, contentDescription = "Добавить задание") }
        },
        containerColor = LightBlue
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = White), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Прогресс: $doneCount / ${shown.size}", color = DeepBlue, fontWeight = FontWeight.SemiBold)
                        if (!filterSubject.isNullOrBlank()) {
                            TextButton(onClick = onClearFilter) { Text("Показать все") }
                        }
                    }
                }
            }
            items(shown) { task ->
                Card(colors = CardDefaults.cardColors(containerColor = White), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.done,
                            onCheckedChange = { onUpdate(task.copy(done = it)) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, fontWeight = FontWeight.SemiBold)
                            Text("${task.subject} • ${task.deadline}")
                            Text("Приоритет: ${task.priority} • Повтор: ${task.repeatMode}")
                        }
                        IconButton(onClick = { editing = task }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                        IconButton(onClick = { onDelete(task.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }
        }
    }

    if (editing != null) {
        TaskEditorDialog(
            task = editing!!,
            onDismiss = { editing = null },
            onSave = { updated ->
                if (tasks.any { it.id == updated.id }) onUpdate(updated) else onAdd(updated)
                editing = null
            }
        )
    }
}

@Composable
private fun TaskEditorDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onSave: (TaskItem) -> Unit
) {
    var title by remember(task.id) { mutableStateOf(task.title) }
    var subject by remember(task.id) { mutableStateOf(task.subject) }
    var deadline by remember(task.id) { mutableStateOf(task.deadline) }
    var priority by remember(task.id) { mutableStateOf(task.priority) }
    var repeat by remember(task.id) { mutableStateOf(task.repeatMode) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showRepeatMenu by remember { mutableStateOf(false) }
    val priorityOptions = listOf("Низкий", "Средний", "Высокий")
    val repeatOptions = listOf("Нет", "Ежедневно", "Еженедельно")
    val dateState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task.title.isBlank()) "Новое задание" else "Редактировать задание") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") })
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Предмет") })
                OutlinedTextField(
                    value = deadline,
                    onValueChange = {},
                    label = { Text("Дедлайн") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )
                ExposedDropdownMenuBox(
                    expanded = showPriorityMenu,
                    onExpandedChange = { showPriorityMenu = !showPriorityMenu }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Приоритет") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    showPriorityMenu = false
                                }
                            )
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = showRepeatMenu,
                    onExpandedChange = { showRepeatMenu = !showRepeatMenu }
                ) {
                    OutlinedTextField(
                        value = repeat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Повтор") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRepeatMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showRepeatMenu,
                        onDismissRequest = { showRepeatMenu = false }
                    ) {
                        repeatOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    repeat = option
                                    showRepeatMenu = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) {
                    onSave(task.copy(title = title, subject = subject, deadline = deadline, priority = priority, repeatMode = repeat))
                }
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = dateState.selectedDateMillis
                    if (millis != null) {
                        val formatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
                        deadline = formatted
                    }
                    showDatePicker = false
                }) { Text("Ок") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Отмена") } }
        ) {
            DatePicker(state = dateState)
        }
    }
}
