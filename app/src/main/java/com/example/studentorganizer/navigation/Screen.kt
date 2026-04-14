package com.example.studentorganizer.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object UniversityContacts : Screen("university_contacts")
    object Support : Screen("support")
    object About : Screen("about")
}
