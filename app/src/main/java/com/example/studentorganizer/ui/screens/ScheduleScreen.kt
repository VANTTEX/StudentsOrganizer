package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.studentorganizer.data.model.ScheduleItem
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    lessons: List<ScheduleItem>,
    onAdd: (ScheduleItem) -> Unit,
    onUpdate: (ScheduleItem) -> Unit,
    onDelete: (String) -> Unit,
    onSubjectClick: (String) -> Unit
) {
    var selectedWeek by remember { mutableStateOf("Верхняя") }
    var showWeekMenu by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ScheduleItem?>(null) }
    val weekOptions = listOf("Верхняя", "Нижняя")
    val filtered = lessons.filter { it.weekType == selectedWeek }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расписание") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = ScheduleItem(weekType = selectedWeek, time = "", subject = "", place = "") },
                containerColor = DeepBlue,
                contentColor = White
            ) { Icon(Icons.Default.Add, contentDescription = "Добавить пару") }
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
                ExposedDropdownMenuBox(
                    expanded = showWeekMenu,
                    onExpandedChange = { showWeekMenu = !showWeekMenu }
                ) {
                    OutlinedTextField(
                        value = selectedWeek,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Неделя") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showWeekMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showWeekMenu,
                        onDismissRequest = { showWeekMenu = false }
                    ) {
                        weekOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedWeek = option
                                    showWeekMenu = false
                                }
                            )
                        }
                    }
                }
            }
            items(filtered) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = White), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.time, color = DeepBlue, fontWeight = FontWeight.SemiBold)
                            Text(item.subject, modifier = Modifier.clickable { onSubjectClick(item.subject) }, fontWeight = FontWeight.Medium)
                            Text(item.place)
                        }
                        IconButton(onClick = { editing = item }) { Icon(Icons.Default.Edit, contentDescription = "Редактировать") }
                        IconButton(onClick = { onDelete(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Удалить") }
                    }
                }
            }
        }
    }

    if (editing != null) {
        ScheduleEditorDialog(
            item = editing!!,
            onDismiss = { editing = null },
            onSave = { updated ->
                if (lessons.any { it.id == updated.id }) onUpdate(updated) else onAdd(updated)
                editing = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleEditorDialog(
    item: ScheduleItem,
    onDismiss: () -> Unit,
    onSave: (ScheduleItem) -> Unit
) {
    var weekType by remember(item.id) { mutableStateOf(item.weekType) }
    var showWeekTypeMenu by remember { mutableStateOf(false) }
    var time by remember(item.id) { mutableStateOf(item.time) }
    var subject by remember(item.id) { mutableStateOf(item.subject) }
    var place by remember(item.id) { mutableStateOf(item.place) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item.time.isBlank()) "Новая пара" else "Редактировать пару") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = showWeekTypeMenu,
                    onExpandedChange = { showWeekTypeMenu = !showWeekTypeMenu }
                ) {
                    OutlinedTextField(
                        value = weekType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Неделя") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showWeekTypeMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showWeekTypeMenu,
                        onDismissRequest = { showWeekTypeMenu = false }
                    ) {
                        listOf("Верхняя", "Нижняя").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    weekType = option
                                    showWeekTypeMenu = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Время") })
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Предмет") })
                OutlinedTextField(value = place, onValueChange = { place = it }, label = { Text("Место") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (time.isNotBlank() && subject.isNotBlank()) {
                    onSave(item.copy(weekType = weekType, time = time, subject = subject, place = place))
                }
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
