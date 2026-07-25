package tv.trakt.trakt.core.main.usecases

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import tv.trakt.trakt.ui.theme.model.CustomTheme

internal val KEY_CUSTOM_THEME_USER_ENABLED = booleanPreferencesKey("key_custom_theme_user_enabled")

internal fun keyCustomThemeUserDismissed(id: String) = booleanPreferencesKey("key_ct_user_dismissed_$id")

internal class CustomThemeUseCase(
    private val mainDataStore: DataStore<Preferences>,
) {
    suspend fun toggleUserEnabled(enabled: Boolean) {
        mainDataStore.edit { prefs ->
            prefs[KEY_CUSTOM_THEME_USER_ENABLED] = enabled
        }
    }

    suspend fun setUserDismissedOverlay(id: String) {
        mainDataStore.edit { prefs ->
            prefs[keyCustomThemeUserDismissed(id)] = true
        }
    }

    // Seasonal themes were driven by Firebase Remote Config, which this fork
    // removes, so no custom theme is ever active.
    suspend fun getConfig(): CustomThemeConfig {
        return CustomThemeConfig(
            visible = false,
            overlayVisible = false,
            enabled = false,
        )
    }

    data class CustomThemeConfig(
        val visible: Boolean,
        val overlayVisible: Boolean,
        val enabled: Boolean,
        val theme: CustomTheme? = null,
    )
}
