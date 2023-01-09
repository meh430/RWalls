package mp.redditwalls.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferenceKeys {
    val DEFAULT_HOME_SORT = stringPreferencesKey("default_home_sort")
    val PREVIEW_RESOLUTION = stringPreferencesKey("preview_resolution")
    val THEME = stringPreferencesKey("theme")
    val REFRESH_ENABLED = booleanPreferencesKey("refresh_enabled")
    val REFRESH_INTERVAL = stringPreferencesKey("refresh")
    val DATA_SETTING = stringPreferencesKey("data_setting") // when to refresh
    val VERTICAL_SWIPE_FEED_ENABLED = booleanPreferencesKey("vertical_swipe_feed_enabled")
    val ALLOW_NSFW = booleanPreferencesKey("allow_nsfw")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val USE_PRESET_FOLDER_FOR_LIKE = booleanPreferencesKey("use_preset_folder_for_like")
    val PRESET_FOLDER_NAME = stringPreferencesKey("preset_folder_name")
}