package com.example.redditwalls.repositories

import android.content.SharedPreferences
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.models.Resolution
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val DEFAULT_SUB = "default_sub"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val LOW_RES_PREVIEWS = "low_res_previews"
        const val RANDOM_REFRESH = "random_refresh"
        const val RANDOM_REFRESH_LOCATION = "random_refresh_location"
    }

    fun setDefaultSub(subreddit: String) {
        prefs.edit().putString(DEFAULT_SUB, subreddit).apply()
    }

    fun getDefaultSub() =
        prefs.getString(DEFAULT_SUB, "").takeIf { !it.isNullOrBlank() } ?: "mobilewallpaper"

    fun setResolution(height: Int, width: Int) {
        prefs.edit().apply {
            putInt(HEIGHT, height)
            putInt(WIDTH, width)
            apply()
        }
    }

    fun getResolution(): Resolution {
        val height = prefs.getInt(HEIGHT, 1920)
        val width = prefs.getInt(WIDTH, 1080)
        return Resolution(height, width)
    }

    fun setLoadLowResPreviews(loadLowRes: Boolean) {
        prefs.edit().putBoolean(LOW_RES_PREVIEWS, loadLowRes).apply()
    }

    fun loadLowResPreviews() = prefs.getBoolean(LOW_RES_PREVIEWS, true)

    fun setRandomRefresh(randomRefresh: Boolean) {
        prefs.edit().putBoolean(RANDOM_REFRESH, randomRefresh).apply()
    }

    fun randomRefreshEnabled() = prefs.getBoolean(RANDOM_REFRESH, false)

    fun getRandomRefreshLocation() =
        WallpaperLocation.fromId(prefs.getInt(RANDOM_REFRESH_LOCATION, 0))

    fun setRandomRefreshLocation(location: WallpaperLocation) =
        prefs.edit().putInt(RANDOM_REFRESH_LOCATION, location.id).apply()

}