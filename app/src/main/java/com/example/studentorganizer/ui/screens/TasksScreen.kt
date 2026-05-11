package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.example.studentorganizer.data.model.TaskPriority
import com.example.studentorganizer.data.model.TaskRepeat
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White
import com.example.studentorganizer.data.model.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    filterSubject: String?,
    onClearFilter: () -> Unit,
    onOpenTask: (String) -> Unit,
    onAddTask: () -> Unit
) {
    val filteredTasks = if (filterSubject.isNullOrBlank()) {
        tasks
    } else {
        tasks.filter { it.subject == filterSubject }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задания") },
                actions = {
                    IconButton(onClick = onAddTask) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
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
            val doneCount = filteredTasks.count { it.isDone }
            val highCount = filteredTasks.count { it.priority == TaskPriority.High }
            val mediumCount = filteredTasks.count { it.priority == TaskPriority.Medium }
            val lowCount = filteredTasks.count { it.priority == TaskPriority.Low }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Прогресс", color = DeepBlue, fontWeight = FontWeight.SemiBold)
                        Text("Выполнено: $doneCount из ${filteredTasks.size}")
                        Text("Приоритеты: высокий $highCount • средний $mediumCount • низкий $lowCount")
                    }
                }
            }
            item {
                Text(
                    text = if (filterSubject.isNullOrBlank()) "Текущие" else "Задания: $filterSubject",
                    style = MaterialTheme.typography.labelLarge,
                    color = DeepBlue
                )
            }
            if (!filterSubject.isNullOrBlank()) {
                item {
                    OutlinedButton(onClick = onClearFilter) {
                        Text("Показать все задания")
                    }
                }
            }
            if (filteredTasks.isEmpty()) {
                item {
                    Text(
                        text = "Заданий пока нет",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(filteredTasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTask(task.id) },
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                task.subject,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = task.deadline,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Приоритет: ${task.priority.toRu()}", style = MaterialTheme.typography.bodySmall)
                        Text("Повтор: ${task.repeat.toRu()}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    task: TaskItem,
    onBack: () -> Unit,
    onSave: (TaskItem) -> Unit,
    onPickSubject: () -> Unit
) {
    var title by remember(task.id, task.title) { mutableStateOf(task.title) }
    var deadline by remember(task.id, task.deadline) { mutableStateOf(task.deadline) }
    var isDone by remember(task.id, task.isDone) { mutableStateOf(task.isDone) }
    var isImportant by remember(task.id, task.isImportant) { mutableStateOf(task.isImportant) }
    var notes by remember(task.id, task.notes) { mutableStateOf(task.notes) }
    var priority by remember(task.id, task.priority) { mutableStateOf(task.priority) }
    var repeat by remember(task.id, task.repeat) { mutableStateOf(task.repeat) }
    var reminderEnabled by remember(task.id, task.reminderEnabled) { mutableStateOf(task.reminderEnabled) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var repeatExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задания") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Text(
                        text = "Готово",
                        color = DeepBlue,
                        modifier = Modifier
                            .clickable {
                                onSave(
                                    task.copy(
                                        title = title,
                                        deadline = deadline,
                                        isDone = isDone,
                                        isImportant = isImportant,
                                        notes = notes,
                                        priority = priority,
                                        repeat = repeat,
                                        reminderEnabled = reminderEnabled
                                    )
                                )
                                onBack()
                            }
                            .padding(horizontal = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = LightBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Название")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                IconButton(onClick = { title = "" }) {
                    Icon(Icons.Default.Close, contentDescription = "Очистить")
                }
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPickSubject() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Предмет")
                Text(task.subject)
            }
            Divider()
            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                label = { Text("Сделать до") }
            )
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Выполнено", modifier = Modifier.weight(1f))
                Switch(
                    checked = isDone,
                    onCheckedChange = { isDone = it }
                )
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Важное", modifier = Modifier.weight(1f))
                Text(
                    text = if (isImportant) "Да" else "Добавить",
                    color = DeepBlue,
                    modifier = Modifier.clickable {
                        isImportant = !isImportant
                    }
                )
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Приоритет")
                Box {
                    Text(
                        text = priority.toRu(),
                        color = DeepBlue,
                        modifier = Modifier.clickable { priorityExpanded = true }
                    )
                    DropdownMenu(expanded = priorityExpanded, onDismissRequest = { priorityExpanded = false }) {
                        TaskPriority.values().forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.toRu()) },
                                onClick = {
                                    priority = item
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Повтор")
                Box {
                    Text(
                        text = repeat.toRu(),
                        color = DeepBlue,
                        modifier = Modifier.clickable { repeatExpanded = true }
                    )
                    DropdownMenu(expanded = repeatExpanded, onDismissRequest = { repeatExpanded = false }) {
                        TaskRepeat.values().forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.toRu()) },
                                onClick = {
                                    repeat = item
                                    repeatExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Уведомление", modifier = Modifier.weight(1f))
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it }
                )
            }
            Divider()
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                label = { Text("Заметка") }
            )
        }
    }
}

private fun TaskPriority.toRu(): String = when (this) {
    TaskPriority.Low -> "Низкий"
    TaskPriority.Medium -> "Средний"
    TaskPriority.High -> "Высокий"
}

private fun TaskRepeat.toRu(): String = when (this) {
    TaskRepeat.None -> "Нет"
    TaskRepeat.Daily -> "Ежедневно"
    TaskRepeat.Weekly -> "Еженедельно"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectPickerScreen(
    subjects: List<String>,
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Предмет") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Text(
                        text = "Готово",
                        color = DeepBlue,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Предмет",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            items(subjects) { subject ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(subject) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(subject)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Divider()
            }
        }
    }
}
