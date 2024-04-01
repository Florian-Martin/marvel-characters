package fr.florianmartin.marvelcharacters.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import fr.florianmartin.marvelcharacters.utils.constants.ETAG
import fr.florianmartin.marvelcharacters.utils.constants.LAST_API_FETCH_TIMESTAMP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DataStoreManager private constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private const val TAG = "DataStoreManager"

        private val LAST_API_CHARACTERS_FETCH_KEY =
            longPreferencesKey(LAST_API_FETCH_TIMESTAMP)

        private val ETAG_KEY = stringPreferencesKey(ETAG)

        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(datastore: DataStore<Preferences>): DataStoreManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(datastore).also { INSTANCE = it }
            }
    }

    private val lastFetchTimestamp: Flow<Long?> = dataStore.data
        .catch { e ->
            Log.e(
                TAG,
                "An error occurred while emitting charactersFetchTimestampFlow: ${e.message}"
            )
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[LAST_API_CHARACTERS_FETCH_KEY]
        }

    private val etag: Flow<String?> = dataStore.data
        .catch { e ->
            e.printStackTrace()
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[ETAG_KEY]
        }

    suspend fun saveLastApiFetchTimestamp(timestamp: Long) {
        dataStore.edit { settings ->
            settings[LAST_API_CHARACTERS_FETCH_KEY] = timestamp
        }
    }

    suspend fun saveEtag(etag: String) {
        dataStore.edit { settings ->
            settings[ETAG_KEY] = etag
        }
    }

    suspend fun getLastFetchTimestamp(): Long? =
        lastFetchTimestamp.firstOrNull()

    suspend fun getEtag(): String? = etag.first()
}