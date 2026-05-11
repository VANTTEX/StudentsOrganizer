package com.example.studentorganizer.ui.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studentorganizer.data.api.UniversityDto
import com.example.studentorganizer.data.model.User
import com.example.studentorganizer.data.repository.AuthRepository
import com.example.studentorganizer.data.repository.ValidationUtil
import com.example.studentorganizer.data.storage.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Аватарка
    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar: StateFlow<Boolean> = _isUploadingAvatar.asStateFlow()

    private val _avatarError = MutableStateFlow<String?>(null)
    val avatarError: StateFlow<String?> = _avatarError.asStateFlow()

    // ВУЗы
    private val _universities = MutableStateFlow<List<UniversityDto>>(emptyList())
    val universities: StateFlow<List<UniversityDto>> = _universities.asStateFlow()

    private val _isSearchingUniversities = MutableStateFlow(false)
    val isSearchingUniversities: StateFlow<Boolean> = _isSearchingUniversities.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepository.isLoggedInFlow.collect { loggedIn ->
                _isLoggedIn.value = loggedIn
            }
        }
        viewModelScope.launch {
            prefsRepository.userFlow.collect { user ->
                _user.value = user
            }
        }
        // Загружаем список ВУЗов при инициализации
        loadUniversities()
    }

    fun login(email: String, password: String) {
        // Локальная валидация
        val emailError = ValidationUtil.validateEmail(email)
        if (emailError != null) {
            _loginError.value = emailError
            return
        }

        val passwordError = ValidationUtil.validatePassword(password)
        if (passwordError != null) {
            _loginError.value = passwordError
            return
        }

        _isLoading.value = true
        _loginError.value = null

        viewModelScope.launch {
            val result = repository.login(email, password)
            _isLoading.value = false

            result.fold(
                onSuccess = { _loginError.value = null },
                onFailure = { _loginError.value = it.message }
            )
        }
    }

    fun register(
        email: String,
        password: String,
        fullName: String,
        course: String?,
        institute: String?,
        confirmPassword: String
    ) {
        // Локальная валидация
        val emailError = ValidationUtil.validateEmail(email)
        if (emailError != null) {
            _registerError.value = emailError
            return
        }

        val passwordError = ValidationUtil.validatePassword(password)
        if (passwordError != null) {
            _registerError.value = passwordError
            return
        }

        val fullNameError = ValidationUtil.validateFullName(fullName)
        if (fullNameError != null) {
            _registerError.value = fullNameError
            return
        }

        val matchError = ValidationUtil.validatePasswordMatch(password, confirmPassword)
        if (matchError != null) {
            _registerError.value = matchError
            return
        }

        _isLoading.value = true
        _registerError.value = null

        viewModelScope.launch {
            val result = repository.register(email, password, fullName, course, institute)
            _isLoading.value = false

            result.fold(
                onSuccess = { _registerError.value = null },
                onFailure = { _registerError.value = it.message }
            )
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            prefsRepository.updateUser(user)
        }
    }

    fun uploadAvatar(uri: Uri, context: Context) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser.id == 0) {
                _avatarError.value = "Пользователь не авторизован"
                return@launch
            }

            _isUploadingAvatar.value = true
            _avatarError.value = null

            val result = repository.uploadAvatar(currentUser.id, uri, context)
            _isUploadingAvatar.value = false

            result.fold(
                onSuccess = { avatarUrl ->
                    // Обновляем user с новым avatarUrl
                    _user.value = currentUser.copy(avatarUrl = avatarUrl)
                },
                onFailure = { _avatarError.value = it.message }
            )
        }
    }

    fun loadUniversities() {
        viewModelScope.launch {
            _isSearchingUniversities.value = true
            val result = repository.getUniversities()
            _isSearchingUniversities.value = false

            result.fold(
                onSuccess = { _universities.value = it },
                onFailure = { /* Тихо игнорируем, ВУЗы не критичны */ }
            )
        }
    }

    fun searchUniversities(query: String) {
        if (query.isBlank()) {
            loadUniversities()
            return
        }
        viewModelScope.launch {
            _isSearchingUniversities.value = true
            val result = repository.searchUniversities(query)
            _isSearchingUniversities.value = false

            result.fold(
                onSuccess = { _universities.value = it },
                onFailure = { /* Тихо игнорируем */ }
            )
        }
    }

    suspend fun getUserData(): User {
        return prefsRepository.userFlow.first()
    }

    fun logout() {
        viewModelScope.launch {
            prefsRepository.logout()
        }
    }

    fun clearErrors() {
        _loginError.value = null
        _registerError.value = null
        _avatarError.value = null
    }

    companion object {
        fun factory(
            repository: AuthRepository,
            prefsRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel(repository, prefsRepository)
            }
        }
    }
}
