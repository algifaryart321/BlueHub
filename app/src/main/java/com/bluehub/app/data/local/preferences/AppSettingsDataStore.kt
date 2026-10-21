package com.bluehub.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "bluehub_settings")

@Singleton
class AppSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val KEY_TEMP_UNIT = stringPreferencesKey("temp_unit")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val autoReconnectFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_RECONNECT] ?: true
    }

    val tempUnitFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_TEMP_UNIT] ?: "°C"
    }

    suspend fun setAutoReconnect(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_RECONNECT] = enabled
        }
    }

    suspend fun setTempUnit(unit: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TEMP_UNIT] = unit
        }
    }
}
