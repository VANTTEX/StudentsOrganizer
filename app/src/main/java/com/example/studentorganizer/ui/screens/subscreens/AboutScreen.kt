package com.example.studentorganizer.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О нас", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Логотип
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(DeepBlue, Color(0xFF4834D4))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "StudentOrganizer",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Версия 1.0",
                        fontSize = 14.sp,
                        color = Color(0xFF8892B0)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "О приложении",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "StudentOrganizer — мобильное приложение-органайзер для студентов, разработанное для удобства управления учебным процессом.",
                        fontSize = 14.sp,
                        color = Color(0xFF555B70),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Возможности",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureRow(Icons.Default.Event, "Расписание занятий")
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(Icons.Default.Task, "Список задач")
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(Icons.Default.Notifications, "Уведомления")
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureRow(Icons.Default.Person, "Личный кабинет")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "© 2026 StudentOrganizer. Все права защищены.",
                fontSize = 12.sp,
                color = Color(0xFF8892B0),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0xFF555B70)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(onNavigateBack = {})
}
