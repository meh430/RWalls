package mp.redditwalls.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mp.redditwalls.preferences.enums.DataSetting
import mp.redditwalls.preferences.enums.ImageQuality
import mp.redditwalls.preferences.enums.RefreshInterval
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.preferences.enums.Theme

class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // getters
    fun getDefaultHomeSort() =
        getValue(PreferenceKeys.DEFAULT_HOME_SORT, SortOrder.HOT.name).map {
            SortOrder.valueOf(it)
        }

    fun getPreviewResolution() =
        getValue(PreferenceKeys.PREVIEW_RESOLUTION, ImageQuality.HIGH.name).map {
            ImageQuality.valueOf(it)
        }

    fun getTheme() = getValue(PreferenceKeys.THEME, Theme.SYSTEM.name).map {
        Theme.valueOf(it)
    }

    fun getRefreshEnabled() = getValue(PreferenceKeys.REFRESH_ENABLED, true)

    fun getRefreshInterval() =
        getValue(PreferenceKeys.REFRESH_INTERVAL, RefreshInterval.SIX_H.name).map {
            RefreshInterval.valueOf(it)
        }

    fun getDataSetting() = getValue(PreferenceKeys.DATA_SETTING, DataSetting.BOTH.name).map {
        DataSetting.valueOf(it)
    }

    fun getVerticalSwipeFeedEnabled() =
        getValue(PreferenceKeys.VERTICAL_SWIPE_FEED_ENABLED, false)

    fun getAllowNsfw() = getValue(PreferenceKeys.ALLOW_NSFW, true)

    // setters
    suspend fun setDefaultHomeSort(sortOrder: SortOrder) {
        setValue(PreferenceKeys.DEFAULT_HOME_SORT, sortOrder.name)
    }

    suspend fun setPreviewResolution(imageQuality: ImageQuality) {
        setValue(PreferenceKeys.PREVIEW_RESOLUTION, imageQuality.name)
    }

    suspend fun setTheme(theme: Theme) {
        setValue(PreferenceKeys.THEME, theme.name)
    }

    suspend fun setRefreshEnabled(refreshEnabled: Boolean) {
        setValue(PreferenceKeys.REFRESH_ENABLED, refreshEnabled)
    }

    suspend fun setRefreshInterval(refreshInterval: RefreshInterval) {
        setValue(PreferenceKeys.REFRESH_INTERVAL, refreshInterval.name)
    }

    suspend fun setDataSetting(dataSetting: DataSetting) {
        setValue(PreferenceKeys.DATA_SETTING, dataSetting.name)
    }

    suspend fun setVerticalSwipeFeedEnabled(verticalSwipeFeedEnabled: Boolean) {
        setValue(PreferenceKeys.VERTICAL_SWIPE_FEED_ENABLED, verticalSwipeFeedEnabled)
    }

    suspend fun setAllowNsfw(allowNsfw: Boolean) {
        setValue(PreferenceKeys.ALLOW_NSFW, allowNsfw)
    }

    private fun <T> getValue(key: Preferences.Key<T>, default: T): Flow<T> =
        Prefs.getDataStore(context).data.map { prefs ->
            prefs[key] ?: default
        }

    private suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        Prefs.getDataStore(context).edit { prefs ->
            prefs[key] = value
        }
    }
}