package com.example.linkedout.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val PROFILE_COMPLETION_STEP_KEY = intPreferencesKey("profile_completion_step")
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] }

    val userId: Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    val userType: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_TYPE_KEY] }

    val profileCompletionStep: Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[PROFILE_COMPLETION_STEP_KEY] }

    suspend fun saveAuthData(token: String, userId: Int, userType: String, profileCompletionStep: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_TYPE_KEY] = userType
            preferences[PROFILE_COMPLETION_STEP_KEY] = profileCompletionStep
        }
    }

    suspend fun updateProfileCompletionStep(step: Int) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_COMPLETION_STEP_KEY] = step
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

