package dev.maples.vm.model.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {
    val FIRST_RUN = booleanPreferencesKey("first_run")
    val firstRunFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_RUN] ?: true
    }

    suspend fun toggleFirstRun() {
        context.dataStore.edit { settings ->
            val firstRun = settings[FIRST_RUN] ?: true
            settings[FIRST_RUN] = !firstRun
        }
    }
}