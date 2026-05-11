package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.studentorganizer.data.model.User
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val onClick: () -> Unit
)

@Composable
fun ProfileScreen(
    user: User,
    onEditProfile: () -> Unit,
    onUniversityContacts: () -> Unit,
    onSupport: () -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        // Шапка с градиентом
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DeepBlue, Color(0xFF4834D4))
                        )
                    )
                    .padding(bottom = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Верхняя панель
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Меню",
                                tint = White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "Личный кабинет",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = White
                        )
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Уведомления",
                                tint = White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Аватар
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = "Аватар",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Аватар",
                                tint = White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Имя
                    Text(
                        text = user.fullName.ifEmpty { "Студент" },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.university.ifEmpty { "ВУЗ не указан" },
                        fontSize = 14.sp,
                        color = Color(0xFFB8B5FF)
                    )
                }
            }
        }

        // Карточка с информацией
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Мой профиль",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlue
                        )
                        Text(
                            text = "Изменить",
                            fontSize = 14.sp,
                            color = DeepBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onEditProfile() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(Icons.Default.School, "Факультет", user.faculty)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(Icons.Default.DateRange, "Курс", user.course)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileInfoRow(Icons.Default.Email, "Email", user.email)
                }
            }
        }

        // Карточка с Friend ID
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
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
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = null,
                            tint = DeepBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ваш ID для друзей",
                                fontSize = 12.sp,
                                color = Color(0xFF8892B0)
                            )
                            Text(
                                text = user.friendId.ifEmpty { "— — —" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepBlue
                            )
                        }
                    }
                }
            }
        }

        // Меню навигации
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Разделы",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    MenuItemRow(
                        icon = Icons.Default.Phone,
                        title = "Контакты ВУЗа",
                        iconColor = Color(0xFF27AE60),
                        onClick = onUniversityContacts
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItemRow(
                        icon = Icons.Default.Help,
                        title = "Поддержка",
                        iconColor = Color(0xFFF5A623),
                        onClick = onSupport
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItemRow(
                        icon = Icons.Default.Info,
                        title = "О нас",
                        iconColor = Color(0xFF6C5CE7),
                        onClick = onAbout
                    )
                }
            }
        }

        // Кнопка выхода
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEB5757)),
                border = null
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Выйти",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = DeepBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF8892B0)
            )
            Text(
                text = value.ifEmpty { "Не указано" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1580)
            )
        }
    }
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1580),
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF8892B0),
            modifier = Modifier.size(24.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        user = User(
            fullName = "Косинов Алексей",
            faculty = "Факультет информатики",
            course = "3 курс",
            email = "kosinov@university.ru",
            university = "КФУ"
        ),
        onEditProfile = {},
        onUniversityContacts = {},
        onSupport = {},
        onAbout = {},
        onLogout = {}
    )
}
