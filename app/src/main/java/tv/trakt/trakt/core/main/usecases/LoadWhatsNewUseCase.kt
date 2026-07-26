package tv.trakt.trakt.core.main.usecases

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import tv.trakt.trakt.common.model.WhatsNew

internal class LoadWhatsNewUseCase(
    private val dataStore: DataStore<Preferences>,
) {
    // What's New content was delivered via Firebase Remote Config, which this
    // fork removes, so there is never anything to show.
    suspend fun getWhatsNew(): WhatsNew? {
        return null
    }

    suspend fun dismissWhatsNew(id: Int) {
        dataStore.edit { prefs ->
            prefs[getPreferenceKey()] = id
        }
    }

    private fun getPreferenceKey() = intPreferencesKey("key_dismiss_whats_new")
}
