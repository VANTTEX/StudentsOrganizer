package com.example.studentorganizer.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentorganizer.data.model.User
import com.example.studentorganizer.data.storage.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isLoggedInFlow.collect { loggedIn ->
                _isLoggedIn.value = loggedIn
            }
        }
        viewModelScope.launch {
            repository.userFlow.collect { user ->
                _user.value = user
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginError.value = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            repository.loginUser(email, password)
            _loginError.value = null
        }
    }

    fun register(user: User, confirmPassword: String) {
        if (user.fullName.isBlank() || user.email.isBlank() || user.password.isBlank() ||
            user.faculty.isBlank() || user.course.isBlank() || user.university.isBlank()) {
            _registerError.value = "Заполните все поля"
            return
        }
        if (user.password != confirmPassword) {
            _registerError.value = "Пароли не совпадают"
            return
        }
        viewModelScope.launch {
            repository.saveUser(user)
            _registerError.value = null
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    suspend fun getUserData(): User {
        return repository.userFlow.first()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun clearErrors() {
        _loginError.value = null
        _registerError.value = null
    }
}
