package com.example.studentorganizer.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.data.model.User
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.White

data class ContactInfo(
    val title: String,
    val icon: ImageVector,
    val value: String,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityContactsScreen(
    user: User,
    onNavigateBack: () -> Unit
) {
    val contacts = listOf(
        ContactInfo("Деканат", Icons.Default.School, "kab-123@university.ru", Color(0xFF27AE60)),
        ContactInfo("Учебный отдел", Icons.Default.LibraryBooks, "study@university.ru", Color(0xFF3498DB)),
        ContactInfo("Приёмная комиссия", Icons.Default.Phone, "+7 (XXX) XXX-XX-XX", Color(0xFFF5A623)),
        ContactInfo("Библиотека", Icons.Default.Book, "library@university.ru", Color(0xFF6C5CE7)),
        ContactInfo("Студенческий отдел", Icons.Default.People, "student@university.ru", Color(0xFFE74C3C))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Контакты ВУЗа", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FF))
        ) {
            // Информация о ВУЗе
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(DeepBlue, Color(0xFF4834D4))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = user.university.ifEmpty { "ВУЗ не указан" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.faculty.ifEmpty { "Факультет не указан" },
                                fontSize = 14.sp,
                                color = Color(0xFF8892B0)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Контакты",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(contacts) { contact ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(contact.iconColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(contact.icon, contentDescription = null, tint = contact.iconColor, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = contact.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1A1580)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = contact.value,
                                    fontSize = 13.sp,
                                    color = Color(0xFF8892B0)
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun UniversityContactsScreenPreview() {
    UniversityContactsScreen(
        user = User(
            fullName = "Косинов Алексей",
            faculty = "Факультет информатики",
            university = "КФУ"
        ),
        onNavigateBack = {}
    )
}
