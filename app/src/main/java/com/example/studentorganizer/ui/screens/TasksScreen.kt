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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.studentorganizer.data.model.TaskItem
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task.title.isBlank()) "Новое задание" else "Редактировать задание") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") })
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Предмет") })
                OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Дедлайн") })
                OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Приоритет") })
                OutlinedTextField(value = repeat, onValueChange = { repeat = it }, label = { Text("Повтор") })
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
}
