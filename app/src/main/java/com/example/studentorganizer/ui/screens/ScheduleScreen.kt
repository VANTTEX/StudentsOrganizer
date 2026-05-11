package com.example.studentorganizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.ui.theme.DeepBlue
import com.example.studentorganizer.ui.theme.LightBlue
import com.example.studentorganizer.ui.theme.White

private enum class WeekType { Upper, Lower }
private data class Lesson(val time: String, val subject: String, val place: String, val upper: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    var week by remember { mutableStateOf(WeekType.Upper) }
    val lessons = listOf(
        Lesson("09:50", "ОПП", "Ауд. 7.11", true),
        Lesson("11:25", "Практика решения задач", "Ауд. 6.6", true),
        Lesson("13:45", "Генератороведение", "Лаб. 632", false),
        Lesson("15:25", "Имитационное моделирование", "Каб. 7001", false)
    ).filter { if (week == WeekType.Upper) it.upper else !it.upper }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расписание") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
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
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = week == WeekType.Upper,
                        onClick = { week = WeekType.Upper },
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Верхняя") }
                    SegmentedButton(
                        selected = week == WeekType.Lower,
                        onClick = { week = WeekType.Lower },
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Нижняя") }
                }
            }
            items(lessons) { lesson ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(lesson.subject, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(lesson.time)
                            Text(lesson.place, color = DeepBlue)
                        }
                    }
                }
            }
        }
    }
}
