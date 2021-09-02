package com.example.redditwalls.repositories

import android.content.SharedPreferences
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.misc.fromId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val FALLBACK_SUBREDDIT = "mobilewallpaper"
        private const val DEFAULT_SUB = "default_sub"
        private const val DEFAULT_SORT = "default_sort"
        private const val LOW_RES_PREVIEWS = "low_res_previews"
        private const val RANDOM_REFRESH = "random_refresh"
        private const val RANDOM_REFRESH_LOCATION = "random_refresh_location"
        private const val REFRESH_PERIOD = "refresh_period"
        private const val REFRESH_INTERVAL = "refresh_interval"
        private const val THEME = "theme"
    }

    fun setDefaultSub(subreddit: String) {
        prefs.edit().putString(DEFAULT_SUB, subreddit).apply()
    }

    fun getDefaultSub() =
        prefs.getString(DEFAULT_SUB, "").takeIf { !it.isNullOrBlank() } ?: FALLBACK_SUBREDDIT

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

    fun setRandomRefreshInterval(interval: RefreshInterval) {
        prefs.edit().putInt(REFRESH_INTERVAL, interval.id).apply()
    }

    fun getRandomRefreshInterval() =
        RefreshInterval.fromId(prefs.getInt(REFRESH_INTERVAL, RefreshInterval.ONE_H.id))


    fun setTheme(theme: Theme) {
        prefs.edit().putInt(THEME, theme.id).apply()
    }

    fun getTheme() = Theme.fromId(prefs.getInt(THEME, Theme.SYSTEM.id))

    fun setDefaultSort(sort: RWApi.Sort) {
        prefs.edit().putInt(DEFAULT_SORT, sort.id).apply()
    }

    fun getDefaultSort() = RWApi.Sort.fromId(prefs.getInt(DEFAULT_SORT, RWApi.Sort.HOT.id))

    fun clearRandomRefreshSettings() {
        prefs.edit().remove(REFRESH_INTERVAL).remove(RANDOM_REFRESH_LOCATION).apply()
    }
}

enum class Theme(override val id: Int, override val displayText: String) : SettingsItem {
    LIGHT(0, "Light mode"),
    DARK(1, "Dark mode"),
    SYSTEM(2, "Follow system");

    companion object {
        fun fromId(id: Int) = values().fromId(id, SYSTEM)
    }
}

enum class RefreshInterval(
    override val id: Int,
    override val displayText: String,
    val timeUnit: TimeUnit,
    val amount: Long
) : SettingsItem {
    FIFTEEN_M(0, "Fifteen minutes", TimeUnit.MINUTES, 15),
    THIRTY_M(1, "Thirty minutes", TimeUnit.MINUTES, 30),
    ONE_H(2, "One hour", TimeUnit.HOURS, 1),
    SIX_H(3, "Six hours", TimeUnit.HOURS, 6),
    TWELVE_H(4, "Twelve hours", TimeUnit.HOURS, 12),
    TWENTY_FOUR_H(5, "Twenty-four hours", TimeUnit.HOURS, 24);

    companion object {
        fun fromId(id: Int) = values().fromId(id, ONE_H)
    }
}

interface SettingsItem {
    val id: Int
    val displayText: String
}