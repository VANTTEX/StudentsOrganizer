package com.example.studentorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentorganizer.data.storage.UserPreferencesRepository
import com.example.studentorganizer.navigation.Screen
import com.example.studentorganizer.ui.screens.*
import com.example.studentorganizer.ui.screens.subscreens.AboutScreen
import com.example.studentorganizer.ui.screens.subscreens.SupportScreen
import com.example.studentorganizer.ui.screens.subscreens.UniversityContactsScreen
import com.example.studentorganizer.ui.theme.StudentOrganizerTheme

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
    var startDestination = if (isLoggedIn) Screen.Profile.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    onLogin(email, password)
                    navController.navigate(Screen.Profile.route) {
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
                    navController.navigate(Screen.Profile.route) {
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

        composable(Screen.Profile.route) {
            ProfileScreen(
                user = user,
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
