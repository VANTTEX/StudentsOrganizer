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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.data.model.NoteChecklistItem
import com.example.studentorganizer.data.model.NoteItem
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

private enum class NotesSort { Updated, Title }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notes: List<NoteItem>,
    onOpenNote: (String) -> Unit,
    onAddNote: () -> Unit,
    onSync: () -> Unit,
    onDeleteNote: (String) -> Unit,
    onRenameFolder: (String, String) -> Unit,
    onDeleteFolder: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(NotesSort.Updated) }
    var selectedFolder by remember { mutableStateOf("Все папки") }
    var folderMenuOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var deletingNoteId by remember { mutableStateOf<String?>(null) }
    var managingFolders by remember { mutableStateOf(false) }

    val folders = listOf("Все папки") + notes.map { it.folder }.distinct().sorted()
    val filtered = notes.filter {
        val inFolder = selectedFolder == "Все папки" || it.folder == selectedFolder
        val inQuery = it.title.contains(query, true) || it.content.contains(query, true)
        inFolder && inQuery
    }.let {
        when (sort) {
            NotesSort.Updated -> it.sortedByDescending { note -> note.updatedAt }
            NotesSort.Title -> it.sortedBy { note -> note.title.lowercase() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заметки") },
                actions = {
                    IconButton(onClick = { managingFolders = true }) {
                        Icon(Icons.Default.Folder, contentDescription = "Папки")
                    }
                    IconButton(onClick = onSync) {
                        Icon(Icons.Default.Sync, contentDescription = "Синхронизация")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote, containerColor = DeepBlue, contentColor = White) {
                Icon(Icons.Default.Add, contentDescription = "Добавить заметку")
            }
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
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Поиск по заметкам") },
                    singleLine = true
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box {
                        FilterChipLike(text = selectedFolder, onClick = { folderMenuOpen = true })
                        DropdownMenu(expanded = folderMenuOpen, onDismissRequest = { folderMenuOpen = false }) {
                            folders.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder) },
                                    onClick = {
                                        selectedFolder = folder
                                        folderMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                    Box {
                        FilterChipLike(
                            text = if (sort == NotesSort.Updated) "Сорт: новые" else "Сорт: А-Я",
                            onClick = { sortMenuOpen = true }
                        )
                        DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                            DropdownMenuItem(text = { Text("Сначала новые") }, onClick = {
                                sort = NotesSort.Updated
                                sortMenuOpen = false
                            })
                            DropdownMenuItem(text = { Text("По названию") }, onClick = {
                                sort = NotesSort.Title
                                sortMenuOpen = false
                            })
                        }
                    }
                }
            }
            if (filtered.isEmpty()) {
                item {
                    Text("Заметок не найдено", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(filtered) { note ->
                NoteCard(
                    note = note,
                    onClick = { onOpenNote(note.id) },
                    onDelete = { deletingNoteId = note.id }
                )
            }
        }
    }

    if (deletingNoteId != null) {
        val deletingTitle = notes.firstOrNull { it.id == deletingNoteId }?.title.orEmpty()
        AlertDialog(
            onDismissRequest = { deletingNoteId = null },
            title = { Text("Удалить заметку?") },
            text = { Text("Заметка \"$deletingTitle\" будет удалена без возможности восстановления.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteNote(deletingNoteId!!)
                    deletingNoteId = null
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { deletingNoteId = null }) { Text("Отмена") }
            }
        )
    }

    if (managingFolders) {
        FolderManagerDialog(
            notes = notes,
            onDismiss = { managingFolders = false },
            onRenameFolder = onRenameFolder,
            onDeleteFolder = onDeleteFolder
        )
    }
}

@Composable
private fun FilterChipLike(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(White, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) { Text(text, color = DeepBlue) }
}

@Composable
private fun NoteCard(note: NoteItem, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = note.backgroundColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(note.title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                note.content.take(90).ifBlank { "Пустая заметка" },
                style = MaterialTheme.typography.bodySmall
            )
            if (note.checklist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                note.checklist.take(2).forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = item.checked, onCheckedChange = null)
                        Text(item.text, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.folder, color = DeepBlue, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить заметку")
                }
            }
        }
    }
}

@Composable
private fun FolderManagerDialog(
    notes: List<NoteItem>,
    onDismiss: () -> Unit,
    onRenameFolder: (String, String) -> Unit,
    onDeleteFolder: (String) -> Unit
) {
    val folders = notes.map { it.folder }.distinct().sorted().filterNot { it == "Общие" }
    var newFolderName by remember { mutableStateOf("") }
    var renameTarget by remember { mutableStateOf<String?>(null) }
    var renameValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Управление папками") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("Новая папка") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                TextButton(
                    onClick = {
                        val trimmed = newFolderName.trim()
                        if (trimmed.isNotBlank()) {
                            onRenameFolder("Общие", trimmed)
                            newFolderName = ""
                        }
                    }
                ) { Text("Создать из \"Общие\"") }

                folders.forEach { folder ->
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(folder, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                renameTarget = folder
                                renameValue = folder
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Переименовать папку")
                            }
                            IconButton(onClick = { onDeleteFolder(folder) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить папку")
                            }
                        }
                        if (renameTarget == folder) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = renameValue,
                                    onValueChange = { renameValue = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                TextButton(onClick = {
                                    val trimmed = renameValue.trim()
                                    if (trimmed.isNotBlank()) onRenameFolder(folder, trimmed)
                                    renameTarget = null
                                }) { Text("Ок") }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Готово") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    note: NoteItem,
    onBack: () -> Unit,
    onSave: (NoteItem) -> Unit
) {
    var title by remember(note.id) { mutableStateOf(note.title) }
    var content by remember(note.id) { mutableStateOf(note.content) }
    var folder by remember(note.id) { mutableStateOf(note.folder) }
    var fontSize by remember(note.id) { mutableStateOf(note.fontSizeSp) }
    var isBold by remember(note.id) { mutableStateOf(note.isBold) }
    var isItalic by remember(note.id) { mutableStateOf(note.isItalic) }
    var color by remember(note.id) { mutableStateOf(note.backgroundColor) }
    var checklist by remember(note.id) { mutableStateOf(note.checklist) }
    var newChecklistItem by remember { mutableStateOf("") }

    val colors = listOf(
        Color(0xFFEAF3FF), Color(0xFFFFF7D6), Color(0xFFE8FBEA), Color(0xFFFFE9EF), Color(0xFFEDE9FF)
    )
    val symbols = listOf("∑", "√", "→", "≤", "≥", "≠", "∞", "±", "Δ", "λ")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заметка") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                },
                actions = {
                    Text(
                        "Готово",
                        color = DeepBlue,
                        modifier = Modifier
                            .clickable {
                                onSave(
                                    note.copy(
                                        title = title,
                                        content = content,
                                        folder = folder.ifBlank { "Общие" },
                                        fontSizeSp = fontSize,
                                        isBold = isBold,
                                        isItalic = isItalic,
                                        backgroundColor = color,
                                        checklist = checklist,
                                        updatedAt = System.currentTimeMillis()
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
        containerColor = color
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Название") })
            OutlinedTextField(folder, { folder = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Папка") })

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Размер: ${fontSize.toInt()}sp")
                IconButton(onClick = { if (fontSize > 12f) fontSize -= 1f }) { Text("-") }
                IconButton(onClick = { if (fontSize < 30f) fontSize += 1f }) { Text("+") }
                IconButton(onClick = { isBold = !isBold }) { Text("B", fontWeight = FontWeight.Bold) }
                IconButton(onClick = { isItalic = !isItalic }) { Text("I", fontStyle = FontStyle.Italic) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(28.dp)
                            .background(c, CircleShape)
                            .clickable { color = c }
                    )
                }
            }

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                label = { Text("Текст заметки") },
                textStyle = TextStyle(
                    fontSize = fontSize.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal
                )
            )

            Text("Спецсимволы", color = DeepBlue, style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                symbols.forEach { symbol ->
                    Box(
                        modifier = Modifier
                            .background(White, RoundedCornerShape(8.dp))
                            .clickable { content += symbol }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) { Text(symbol) }
                }
            }

            Text("Список (checkbox)", color = DeepBlue, style = MaterialTheme.typography.labelLarge)
            checklist.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = item.checked,
                        onCheckedChange = { checked ->
                            checklist = checklist.map {
                                if (it.id == item.id) it.copy(checked = checked) else it
                            }
                        }
                    )
                    OutlinedTextField(
                        value = item.text,
                        onValueChange = { text ->
                            checklist = checklist.map {
                                if (it.id == item.id) it.copy(text = text) else it
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newChecklistItem,
                    onValueChange = { newChecklistItem = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Новый пункт") },
                    singleLine = true
                )
                IconButton(onClick = {
                    if (newChecklistItem.isNotBlank()) {
                        checklist = checklist + NoteChecklistItem(text = newChecklistItem)
                        newChecklistItem = ""
                    }
                }) { Icon(Icons.Default.Add, contentDescription = "Добавить пункт") }
            }
        }
    }
}
