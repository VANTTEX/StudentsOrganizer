package com.example.studentorganizer.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Schedule : Screen("schedule")
    object Tasks : Screen("tasks")
    object Notes : Screen("notes")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    object EditProfile : Screen("edit_profile")
    object UniversityContacts : Screen("university_contacts")
    object Support : Screen("support")
    object About : Screen("about")
}
