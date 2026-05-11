package com.example.studentorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentorganizer.data.model.ScheduleLesson
import com.example.studentorganizer.data.model.TaskItem
import com.example.studentorganizer.data.model.defaultLessons
import com.example.studentorganizer.data.model.defaultNotes
import com.example.studentorganizer.data.model.defaultTasks
import com.example.studentorganizer.notifications.TaskNotificationHelper
import com.example.studentorganizer.data.storage.StudyDataRepository
import com.example.studentorganizer.data.storage.UserPreferencesRepository
import com.example.studentorganizer.navigation.Screen
import com.example.studentorganizer.ui.screens.*
import com.example.studentorganizer.ui.screens.subscreens.AboutScreen
import com.example.studentorganizer.ui.screens.subscreens.SupportScreen
import com.example.studentorganizer.ui.screens.subscreens.UniversityContactsScreen
import com.example.studentorganizer.ui.theme.StudentOrganizerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentOrganizerTheme {
                val navController = rememberNavController()
                val repository = UserPreferencesRepository(this)
                val viewModel: AuthViewModel = viewModel {
                    AuthViewModel(repository)
                }

                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val user by viewModel.user.collectAsState()
                val loginError by viewModel.loginError.collectAsState()
                val registerError by viewModel.registerError.collectAsState()

                AppNavigation(
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    user = user,
                    loginError = loginError,
                    registerError = registerError,
                    onLogin = viewModel::login,
                    onRegister = viewModel::register,
                    onUpdateProfile = viewModel::updateProfile,
                    onLogout = viewModel::logout,
                    onClearErrors = viewModel::clearErrors
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    isLoggedIn: Boolean,
    user: com.example.studentorganizer.data.model.User,
    loginError: String?,
    registerError: String?,
    onLogin: (String, String) -> Unit,
    onRegister: (com.example.studentorganizer.data.model.User, String) -> Unit,
    onUpdateProfile: (com.example.studentorganizer.data.model.User) -> Unit,
    onLogout: () -> Unit,
    onClearErrors: () -> Unit
) {
    val startDestination = if (isLoggedIn) Screen.Tasks.route else Screen.Login.route
    val studyDataRepository = remember { StudyDataRepository(navController.context) }
    val notificationHelper = remember { TaskNotificationHelper(navController.context) }
    val coroutineScope = rememberCoroutineScope()
    val tasks by studyDataRepository.tasksFlow.collectAsState(initial = defaultTasks())
    val lessons by studyDataRepository.lessonsFlow.collectAsState(initial = defaultLessons())
    val notes by studyDataRepository.notesFlow.collectAsState(initial = defaultNotes())
    var editingTaskId by remember { mutableStateOf<String?>(null) }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var tasksFilterSubject by remember { mutableStateOf<String?>(null) }
    val subjects = listOf(
        "Английский",
        "Большие данные",
        "Большие данные. Лабораторная",
        "Веб",
        "Веб технологии",
        "Генератороведение",
        "Имитационное Моделирование",
        "МУАИ",
        "МФОИ"
    )
    var selectedSubject by remember { mutableStateOf(subjects.first()) }

    LaunchedEffect(Unit) {
        notificationHelper.ensureChannel()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    onLogin(email, password)
                    navController.navigate(Screen.Tasks.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    onClearErrors()
                    navController.navigate(Screen.Register.route)
                },
                error = loginError
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegister = { newUser, confirmPassword ->
                    onRegister(newUser, confirmPassword)
                    navController.navigate(Screen.Tasks.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    onClearErrors()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                error = registerError
            )
        }

        composable(Screen.Tasks.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Tasks.route) {
                TasksScreen(
                    tasks = tasks,
                    filterSubject = tasksFilterSubject,
                    onClearFilter = { tasksFilterSubject = null },
                    onOpenTask = { taskId ->
                        editingTaskId = taskId
                        navController.navigate(Screen.TaskEditor.route)
                    },
                    onAddTask = {
                        val newTask = TaskItem(
                            title = "Новое задание",
                            subject = selectedSubject,
                            deadline = ""
                        )
                        coroutineScope.launch {
                            studyDataRepository.saveTasks(listOf(newTask) + tasks)
                        }
                        editingTaskId = newTask.id
                        navController.navigate(Screen.TaskEditor.route)
                    }
                )
            }
        }

        composable(Screen.Notes.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Notes.route) {
                NotesScreen(
                    notes = notes,
                    onOpenNote = { noteId ->
                        editingNoteId = noteId
                        navController.navigate(Screen.NoteEditor.route)
                    },
                    onAddNote = {
                        val newNote = com.example.studentorganizer.data.model.NoteItem(
                            title = "Новая заметка",
                            content = "",
                            folder = "Общие"
                        )
                        coroutineScope.launch {
                            studyDataRepository.saveNotes(listOf(newNote) + notes)
                        }
                        editingNoteId = newNote.id
                        navController.navigate(Screen.NoteEditor.route)
                    },
                    onSync = {
                        coroutineScope.launch {
                            studyDataRepository.saveNotes(notes)
                        }
                    },
                    onDeleteNote = { noteId ->
                        coroutineScope.launch {
                            studyDataRepository.saveNotes(notes.filterNot { it.id == noteId })
                        }
                    },
                    onRenameFolder = { fromFolder, toFolder ->
                        coroutineScope.launch {
                            val next = notes.map { note ->
                                if (note.folder == fromFolder) note.copy(folder = toFolder) else note
                            }
                            studyDataRepository.saveNotes(next)
                        }
                    },
                    onDeleteFolder = { folder ->
                        coroutineScope.launch {
                            val next = notes.map { note ->
                                if (note.folder == folder) note.copy(folder = "Общие") else note
                            }
                            studyDataRepository.saveNotes(next)
                        }
                    }
                )
            }
        }

        composable(Screen.Schedule.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Schedule.route) {
                ScheduleScreen(
                    lessons = lessons,
                    onSubjectClick = { subject ->
                        tasksFilterSubject = subject
                        navController.navigate(Screen.Tasks.route) {
                            launchSingleTop = true
                        }
                    },
                    onSync = {
                        coroutineScope.launch {
                            studyDataRepository.saveLessons(lessons)
                        }
                    },
                    onAdd = {
                        val newLesson = ScheduleLesson(
                            startTime = "10:00",
                            endTime = "11:35",
                            subject = "Новый предмет",
                            place = "Ауд. уточняется",
                            weekType = com.example.studentorganizer.data.model.WeekType.Upper,
                            dayTitle = "Понедельник 16 февр. 2026 г.",
                            accentColor = androidx.compose.ui.graphics.Color(0xFFCE93D8)
                        )
                        coroutineScope.launch {
                            studyDataRepository.saveLessons(lessons + newLesson)
                        }
                    },
                    onUpdateLesson = { oldLesson, updatedLesson ->
                        coroutineScope.launch {
                            val next = lessons.map { if (it == oldLesson) updatedLesson else it }
                            studyDataRepository.saveLessons(next)
                        }
                    },
                    onDeleteLesson = { lesson ->
                        coroutineScope.launch {
                            studyDataRepository.saveLessons(lessons.filterNot { it == lesson })
                        }
                    }
                )
            }
        }

        composable(Screen.NoteEditor.route) {
            val currentNote = notes.firstOrNull { it.id == editingNoteId }
            if (currentNote != null) {
                NoteEditorScreen(
                    note = currentNote,
                    onBack = { navController.popBackStack() },
                    onSave = { updated ->
                        coroutineScope.launch {
                            val next = notes.map { if (it.id == updated.id) updated else it }
                            studyDataRepository.saveNotes(next)
                        }
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Profile.route) {
                ProfileScreen(
                    user = user,
                    onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onUniversityContacts = { navController.navigate(Screen.UniversityContacts.route) },
                    onSupport = { navController.navigate(Screen.Support.route) },
                    onAbout = { navController.navigate(Screen.About.route) },
                    onLogout = {
                        onLogout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.TaskEditor.route) {
            val currentTask = tasks.firstOrNull { it.id == editingTaskId }
            if (currentTask != null) {
                TaskEditorScreen(
                    task = currentTask,
                    onBack = { navController.popBackStack() },
                    onSave = { updated ->
                        coroutineScope.launch {
                            val next = tasks.map { if (it.id == updated.id) updated else it }
                            studyDataRepository.saveTasks(next)
                            if (updated.reminderEnabled) {
                                notificationHelper.notifyReminderEnabled(updated)
                            }
                        }
                    },
                    onPickSubject = { navController.navigate(Screen.SubjectPicker.route) }
                )
            }
        }

        composable(Screen.SubjectPicker.route) {
            SubjectPickerScreen(
                subjects = subjects,
                onBack = { navController.popBackStack() },
                onSelect = { subject ->
                    selectedSubject = subject
                    val currentTask = tasks.firstOrNull { it.id == editingTaskId } ?: return@SubjectPickerScreen
                    coroutineScope.launch {
                        val next = tasks.map {
                            if (it.id == currentTask.id) it.copy(subject = subject) else it
                        }
                        studyDataRepository.saveTasks(next)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                user = user,
                onSave = onUpdateProfile,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.UniversityContacts.route) {
            UniversityContactsScreen(
                user = user,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Support.route) {
            SupportScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.About.route) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun MainTabsScaffold(
    navController: NavHostController,
    selectedRoute: String,
    content: @Composable () -> Unit
) {
    val tabItems = listOf(
        Triple(Screen.Schedule.route, "Расписание", Icons.Default.DateRange),
        Triple(Screen.Tasks.route, "Задания", Icons.Default.Task),
        Triple(Screen.Notes.route, "Заметки", Icons.Default.Description),
        Triple(Screen.Profile.route, "Настройки", Icons.Default.Person)
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabItems.forEach { (route, title, icon) ->
                    NavigationBarItem(
                        selected = selectedRoute == route,
                        onClick = {
                            if (selectedRoute != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(Screen.Tasks.route) { saveState = true }
                                }
                            }
                        },
                        icon = { Icon(icon, contentDescription = title) },
                        label = { androidx.compose.material3.Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) { content() }
    }
}
