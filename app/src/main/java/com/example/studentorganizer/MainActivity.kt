package com.example.studentorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentorganizer.data.model.defaultNotes
import com.example.studentorganizer.data.model.defaultSchedule
import com.example.studentorganizer.data.model.defaultTasks
import com.example.studentorganizer.data.repository.AuthRepository
import com.example.studentorganizer.data.storage.StudyDataRepository
import com.example.studentorganizer.data.storage.UserPreferencesRepository
import com.example.studentorganizer.navigation.Screen
import com.example.studentorganizer.ui.screens.*
import com.example.studentorganizer.ui.screens.subscreens.AboutScreen
import com.example.studentorganizer.ui.screens.subscreens.NotificationsScreen
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
                val prefsRepository = UserPreferencesRepository(this)
                val authRepository = AuthRepository(prefsRepository = prefsRepository)
                
                val viewModel: AuthViewModel = viewModel {
                    AuthViewModel(authRepository, prefsRepository, this@MainActivity)
                }

                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val user by viewModel.user.collectAsState()
                val loginError by viewModel.loginError.collectAsState()
                val registerError by viewModel.registerError.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val isUploadingAvatar by viewModel.isUploadingAvatar.collectAsState()
                val avatarError by viewModel.avatarError.collectAsState()

                AppNavigation(
                    navController = navController,
                    viewModel = viewModel,
                    isLoggedIn = isLoggedIn,
                    user = user,
                    loginError = loginError,
                    registerError = registerError,
                    isLoading = isLoading,
                    isUploadingAvatar = isUploadingAvatar,
                    avatarError = avatarError,
                    onLogin = { email, password, _, _, _, _ ->
                        viewModel.login(email, password)
                    },
                    onRegister = viewModel::register,
                    onUpdateProfile = viewModel::updateProfile,
                    onAvatarSelected = { uri -> viewModel.uploadAvatar(uri, this@MainActivity) },
                    onSearchUniversities = viewModel::searchUniversities,
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
    viewModel: AuthViewModel,
    isLoggedIn: Boolean,
    user: com.example.studentorganizer.data.model.User,
    loginError: String?,
    registerError: String?,
    isLoading: Boolean,
    isUploadingAvatar: Boolean = false,
    avatarError: String? = null,
    onLogin: (String, String, String, String?, String?, String) -> Unit,
    onRegister: (String, String, String, String?, String?, String) -> Unit,
    onUpdateProfile: (com.example.studentorganizer.data.model.User) -> Unit,
    onAvatarSelected: (android.net.Uri) -> Unit = {},
    onSearchUniversities: (String) -> Unit = {},
    onLogout: () -> Unit,
    onClearErrors: () -> Unit
) {
    val startDestination = if (isLoggedIn) Screen.Schedule.route else Screen.Login.route
    val studyDataRepository = remember(user.email) {
        StudyDataRepository(
            context = navController.context,
            accountScope = user.email.ifBlank { "guest" }
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val tasks by studyDataRepository.tasksFlow.collectAsState(initial = defaultTasks())
    val schedule by studyDataRepository.scheduleFlow.collectAsState(initial = defaultSchedule())
    val notes by studyDataRepository.notesFlow.collectAsState(initial = defaultNotes())
    var tasksFilterSubject by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoggedIn) {
        val currentRoute = navController.currentDestination?.route
        if (isLoggedIn && (currentRoute == Screen.Login.route || currentRoute == Screen.Register.route)) {
            navController.navigate(Screen.Profile.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    onLogin(email, password, "", null, null, "")
                },
                onNavigateToRegister = {
                    onClearErrors()
                    navController.navigate(Screen.Register.route)
                },
                error = loginError,
                isLoading = isLoading
            )
        }

        composable(Screen.Register.route) {
            val universities by viewModel.universities.collectAsState()
            RegisterScreen(
                onRegister = onRegister,
                onNavigateToLogin = {
                    onClearErrors()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                error = registerError,
                isLoading = isLoading,
                universities = universities
            )
        }

        composable(Screen.Schedule.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Schedule.route) {
                ScheduleScreen(
                    lessons = schedule,
                    onAdd = { item ->
                        coroutineScope.launch { studyDataRepository.saveSchedule(schedule + item) }
                    },
                    onUpdate = { item ->
                        coroutineScope.launch {
                            studyDataRepository.saveSchedule(schedule.map { if (it.id == item.id) item else it })
                        }
                    },
                    onDelete = { id ->
                        coroutineScope.launch { studyDataRepository.saveSchedule(schedule.filterNot { it.id == id }) }
                    },
                    onSubjectClick = { subject ->
                        tasksFilterSubject = subject
                        navController.navigate(Screen.Tasks.route)
                    }
                )
            }
        }

        composable(Screen.Tasks.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Tasks.route) {
                TasksScreen(
                    tasks = tasks,
                    filterSubject = tasksFilterSubject,
                    onClearFilter = { tasksFilterSubject = null },
                    onAdd = { item ->
                        coroutineScope.launch { studyDataRepository.saveTasks(tasks + item) }
                    },
                    onUpdate = { item ->
                        coroutineScope.launch {
                            studyDataRepository.saveTasks(tasks.map { if (it.id == item.id) item else it })
                        }
                    },
                    onDelete = { id ->
                        coroutineScope.launch { studyDataRepository.saveTasks(tasks.filterNot { it.id == id }) }
                    }
                )
            }
        }

        composable(Screen.Notes.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Notes.route) {
                NotesScreen(
                    notes = notes,
                    onAdd = { item ->
                        coroutineScope.launch { studyDataRepository.saveNotes(notes + item) }
                    },
                    onUpdate = { item ->
                        coroutineScope.launch {
                            studyDataRepository.saveNotes(notes.map { if (it.id == item.id) item else it })
                        }
                    },
                    onDelete = { id ->
                        coroutineScope.launch { studyDataRepository.saveNotes(notes.filterNot { it.id == id }) }
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            MainTabsScaffold(navController = navController, selectedRoute = Screen.Profile.route) {
                ProfileScreen(
                    user = user,
                    onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                    onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onUniversityContacts = { navController.navigate(Screen.UniversityContacts.route) },
                    onSupport = { navController.navigate(Screen.Support.route) },
                    onAbout = { navController.navigate(Screen.About.route) },
                    onLogout = {
                        onLogout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.EditProfile.route) {
            val universities by viewModel.universities.collectAsState()
            EditProfileScreen(
                user = user,
                onSave = onUpdateProfile,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                onAvatarSelected = onAvatarSelected,
                universities = universities,
                onSearchUniversities = onSearchUniversities,
                isUploadingAvatar = isUploadingAvatar,
                avatarErrorMessage = avatarError,
                onAvatarErrorCleared = onClearErrors
            )
        }

        composable(Screen.UniversityContacts.route) {
            val universities by viewModel.universities.collectAsState()
            UniversityContactsScreen(
                user = user,
                universities = universities,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Support.route) {
            SupportScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onNavigateBack = { navController.popBackStack() })
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
    val tabs = listOf(
        Triple(Screen.Schedule.route, "Расписание", Icons.Default.DateRange),
        Triple(Screen.Tasks.route, "Задания", Icons.Default.Task),
        Triple(Screen.Notes.route, "Заметки", Icons.Default.Description),
        Triple(Screen.Profile.route, "Настройки", Icons.Default.Person)
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = selectedRoute == route,
                        onClick = {
                            if (selectedRoute != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) { content() }
    }
}
