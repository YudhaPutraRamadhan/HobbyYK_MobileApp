package com.example.hobbyyk_new.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserStore(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("user_token")
        val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")

        val ROLE_KEY = stringPreferencesKey("user_role")
        val USER_ID_KEY = intPreferencesKey("user_id")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ROLE_KEY]
    }

    val userId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val isFirstTime: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_FIRST_TIME_KEY] ?: true
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveSession(token: String, role: String, id: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROLE_KEY] = role
            preferences[USER_ID_KEY] = id
        }
    }

    suspend fun setFirstTimeFinished() {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_KEY] = false
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(ROLE_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }
}