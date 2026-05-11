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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.data.model.NoteItem
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notes: List<NoteItem>,
    onAdd: (NoteItem) -> Unit,
    onUpdate: (NoteItem) -> Unit,
    onDelete: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<NoteItem?>(null) }
    val filtered = notes.filter { it.title.contains(query, true) || it.content.contains(query, true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заметки") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = NoteItem(title = "", content = "") },
                containerColor = DeepBlue,
                contentColor = White
            ) { Icon(Icons.Default.Add, contentDescription = "Добавить заметку") }
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
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Поиск заметок") }
                )
            }
            items(filtered) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editing = note },
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(note.title, fontWeight = FontWeight.SemiBold)
                            Text(note.content)
                        }
                        IconButton(onClick = { editing = note }) { Icon(Icons.Default.Edit, contentDescription = "Редактировать") }
                        IconButton(onClick = { onDelete(note.id) }) { Icon(Icons.Default.Delete, contentDescription = "Удалить") }
                    }
                }
            }
        }
    }

    if (editing != null) {
        NoteEditorDialog(
            note = editing!!,
            onDismiss = { editing = null },
            onSave = { updated ->
                if (notes.any { it.id == updated.id }) onUpdate(updated) else onAdd(updated)
                editing = null
            }
        )
    }
}

@Composable
private fun NoteEditorDialog(
    note: NoteItem,
    onDismiss: () -> Unit,
    onSave: (NoteItem) -> Unit
) {
    var title by remember(note.id) { mutableStateOf(note.title) }
    var content by remember(note.id) { mutableStateOf(note.content) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (note.title.isBlank()) "Новая заметка" else "Редактировать заметку") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Заголовок") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Текст") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) onSave(note.copy(title = title, content = content, updatedAt = System.currentTimeMillis()))
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
