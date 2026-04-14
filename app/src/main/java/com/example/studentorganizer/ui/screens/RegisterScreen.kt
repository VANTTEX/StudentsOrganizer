package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.data.model.User
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.White

@Composable
fun RegisterScreen(
    onRegister: (User, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    error: String?
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var faculty by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var showUniversityDropdown by remember { mutableStateOf(false) }
    var showCourseDropdown by remember { mutableStateOf(false) }

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

    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс", "6 курс")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlue, Color(0xFF4834D4))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Регистрация",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Создайте аккаунт",
                    fontSize = 14.sp,
                    color = Color(0xFFB8B5FF)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DeepBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Пароль
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Пароль") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DeepBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Подтверждение пароля
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Подтвердите пароль") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DeepBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
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
                                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = DeepBlue) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCourseDropdown = true },
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true,
                                trailingIcon = {
                                    Text("▼", color = DeepBlue)
                                }
                            )
                            DropdownMenu(
                                expanded = showCourseDropdown,
                                onDismissRequest = { showCourseDropdown = false },
                                modifier = Modifier.background(White)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showUniversityDropdown = true },
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true,
                                trailingIcon = {
                                    Text("▼", color = DeepBlue)
                                }
                            )
                            DropdownMenu(
                                expanded = showUniversityDropdown,
                                onDismissRequest = { showUniversityDropdown = false },
                                modifier = Modifier.background(White)
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

                        if (error != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Кнопка регистрации
                        Button(
                            onClick = {
                                val user = User(
                                    fullName = fullName,
                                    email = email,
                                    password = password,
                                    faculty = faculty,
                                    course = course,
                                    university = university
                                )
                                onRegister(user, confirmPassword)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
                        ) {
                            Text("Зарегистрироваться", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ссылка на вход
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Уже есть аккаунт?",
                                fontSize = 14.sp,
                                color = Color(0xFF8892B0)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Войти",
                                fontSize = 14.sp,
                                color = DeepBlue,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateToLogin() }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegister = { _, _ -> },
        onNavigateToLogin = {},
        error = null
    )
}
