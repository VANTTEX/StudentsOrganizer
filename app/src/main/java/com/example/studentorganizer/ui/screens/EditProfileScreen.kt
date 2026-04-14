package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.data.model.User
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: User,
    onSave: (User) -> Unit,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var faculty by remember { mutableStateOf(user.faculty) }
    var course by remember { mutableStateOf(user.course) }
    var university by remember { mutableStateOf(user.university) }
    var showCourseDropdown by remember { mutableStateOf(false) }
    var showUniversityDropdown by remember { mutableStateOf(false) }

    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс", "6 курс")
    val universities = listOf(
        "МГУ им. М.В. Ломоносова",
        "СПбГУ",
        "МФТИ",
        "НИУ ВШЭ",
        "МГТУ им. Н.Э. Баумана",
        "МГИМО",
        "РЭУ им. Г.В. Плеханова",
        "Финансовый университет",
        "СПбПУ",
        "НГУ",
        "ТГУ",
        "КФУ",
        "УрФУ",
        "ЮФУ",
        "РУДН",
        "МАИ",
        "НИТУ МИСиС",
        "Университет ИТМО",
        "ЛЭТИ"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlue,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FF))
                .padding(20.dp)
        ) {
            // Аватар
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Аватар",
                            tint = White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Личные данные",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // ФИО
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("ФИО") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DeepBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Email (только для чтения)
                        OutlinedTextField(
                            value = user.email,
                            onValueChange = {},
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DeepBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFE8ECF4),
                                disabledTextColor = Color(0xFF8892B0)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Факультет
                        OutlinedTextField(
                            value = faculty,
                            onValueChange = { faculty = it },
                            label = { Text("Факультет") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = DeepBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Курс (Dropdown)
                        Box {
                            OutlinedTextField(
                                value = course,
                                onValueChange = {},
                                label = { Text("Курс") },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = DeepBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true,
                                trailingIcon = { Text("▼", color = DeepBlue) }
                            )
                            DropdownMenu(
                                expanded = showCourseDropdown,
                                onDismissRequest = { showCourseDropdown = false }
                            ) {
                                courses.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c) },
                                        onClick = {
                                            course = c
                                            showCourseDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // ВУЗ (Dropdown)
                        Box {
                            OutlinedTextField(
                                value = university,
                                onValueChange = {},
                                label = { Text("ВУЗ") },
                                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = DeepBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true,
                                trailingIcon = { Text("▼", color = DeepBlue) }
                            )
                            DropdownMenu(
                                expanded = showUniversityDropdown,
                                onDismissRequest = { showUniversityDropdown = false }
                            ) {
                                universities.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u, fontSize = 12.sp) },
                                        onClick = {
                                            university = u
                                            showUniversityDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Кнопка сохранения
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val updatedUser = user.copy(
                            fullName = fullName,
                            faculty = faculty,
                            course = course,
                            university = university
                        )
                        onSave(updatedUser)
                        onSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сохранить", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(
        user = User(
            fullName = "Косинов Алексей",
            email = "kosinov@university.ru",
            faculty = "Факультет информатики",
            course = "3 курс",
            university = "КФУ"
        ),
        onSave = {},
        onNavigateBack = {},
        onSuccess = {}
    )
}
