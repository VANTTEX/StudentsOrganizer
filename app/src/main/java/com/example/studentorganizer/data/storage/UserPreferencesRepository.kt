package com.example.studentorganizer.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.studentorganizer.data.api.UserDto
import com.example.studentorganizer.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val FULL_NAME = stringPreferencesKey("full_name")
        private val EMAIL = stringPreferencesKey("email")
        private val PASSWORD = stringPreferencesKey("password")
        private val FACULTY = stringPreferencesKey("faculty")
        private val COURSE = stringPreferencesKey("course")
        private val UNIVERSITY = stringPreferencesKey("university")
        private val AVATAR_URL = stringPreferencesKey("avatar_url")
    }

    val userFlow: Flow<User> = context.dataStore.data.map { prefs ->
        User(
            fullName = prefs[FULL_NAME] ?: "",
            email = prefs[EMAIL] ?: "",
            password = prefs[PASSWORD] ?: "",
            faculty = prefs[FACULTY] ?: "",
            course = prefs[COURSE] ?: "",
            university = prefs[UNIVERSITY] ?: "",
            avatarUrl = prefs[AVATAR_URL] ?: ""
        )
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        context.dataStore.edit { prefs ->
            val storedEmail = prefs[EMAIL] ?: ""
            val storedPassword = prefs[PASSWORD] ?: ""
            if (storedEmail == email && storedPassword == password) {
                prefs[IS_LOGGED_IN] = true
            }
        }
        return true
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[FULL_NAME] = user.fullName
            prefs[EMAIL] = user.email
            prefs[PASSWORD] = user.password
            prefs[FACULTY] = user.faculty
            prefs[COURSE] = user.course
            prefs[UNIVERSITY] = user.university
            prefs[AVATAR_URL] = user.avatarUrl
            prefs[IS_LOGGED_IN] = true
        }
    }

    suspend fun updateUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[FULL_NAME] = user.fullName
            prefs[FACULTY] = user.faculty
            prefs[COURSE] = user.course
            prefs[UNIVERSITY] = user.university
            prefs[AVATAR_URL] = user.avatarUrl
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun saveUserFromServer(userDto: UserDto) {
        context.dataStore.edit { prefs ->
            prefs[FULL_NAME] = userDto.fullName
            prefs[EMAIL] = userDto.email
            prefs[COURSE] = userDto.course ?: ""
            prefs[UNIVERSITY] = userDto.institute ?: ""
            // Пароль не сохраняем локально (он хеширован на сервере)
            prefs[PASSWORD] = ""
            prefs[FACULTY] = ""
            prefs[AVATAR_URL] = ""
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
