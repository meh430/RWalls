package mp.redditwalls.repositories

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import mp.redditwalls.WallpaperLocation
import mp.redditwalls.datasources.RWApi
import mp.redditwalls.utils.fromId
import mp.redditwalls.utils.putValue

class SettingsRepository @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val FALLBACK_SUBREDDIT = "mobilewallpaper"
        private const val DEFAULT_SUB = "default_sub"
        private const val DEFAULT_SORT = "default_sort"
        private const val LOW_RES_PREVIEWS = "low_res_previews"
        private const val RANDOM_REFRESH = "random_refresh"
        private const val RANDOM_REFRESH_LOCATION = "random_refresh_location"
        private const val REFRESH_INTERVAL = "refresh_interval"
        private const val THEME = "theme"
        private const val COLUMN_COUNT = "column_count"
        private const val ANIMATION_ENABLED = "animation_enabled"

        private const val SPECIFY_HOME = "specify_home" // what is loaded in home screen
        private const val RANDOM_ORDER = "random_order" // order in which refresh occurs
        private const val REFRESH_INDEX = "refresh_index"
        private const val TOAST_ENABLED = "toast_enabled"
        private const val FEED_URL = "feed_url"

        private const val PREV_RANDOM_REFRESH = "prev_random_refresh"
        private const val ALLOW_NSFW = "allow_nsfw"
        private const val SWIPE_ENABLED = "swipe_enabled"
    }

    fun setFeedURL(feed: String) {
        prefs.putValue(FEED_URL, feed)
    }

    fun getFeedURL() = prefs.getString(FEED_URL, FALLBACK_SUBREDDIT) ?: FALLBACK_SUBREDDIT


    fun setSpecifyHome(specifyHome: Boolean) {
        prefs.putValue(SPECIFY_HOME, specifyHome)
    }

    fun specifyHome() = prefs.getBoolean(SPECIFY_HOME, true)

    fun setRandomOrder(randomOrder: Boolean) {
        prefs.putValue(RANDOM_ORDER, randomOrder)
    }

    fun randomOrder() = prefs.getBoolean(RANDOM_ORDER, true)

    fun setRefreshIndex(index: Int) {
        prefs.putValue(REFRESH_INDEX, index)
    }

    fun getRefreshIndex() = prefs.getInt(REFRESH_INDEX, 0)

    fun setToastEnabled(toastEnabled: Boolean) {
        prefs.putValue(TOAST_ENABLED, toastEnabled)
    }

    fun toastEnabled() = prefs.getBoolean(TOAST_ENABLED, true)

    fun setDefaultSub(subreddit: String) {
        prefs.putValue(DEFAULT_SUB, subreddit)
    }

    fun getDefaultSub() =
        prefs.getString(DEFAULT_SUB, FALLBACK_SUBREDDIT) ?: FALLBACK_SUBREDDIT

    fun setLoadLowResPreviews(loadLowRes: Boolean) {
        prefs.putValue(LOW_RES_PREVIEWS, loadLowRes)
    }

    fun loadLowResPreviews() = prefs.getBoolean(LOW_RES_PREVIEWS, true)

    fun setRandomRefresh(randomRefresh: Boolean) {
        prefs.putValue(RANDOM_REFRESH, randomRefresh)
    }

    fun randomRefreshEnabled() = prefs.getBoolean(RANDOM_REFRESH, false)

    fun prevRandomRefreshEnabled() = prefs.getBoolean(PREV_RANDOM_REFRESH, false)

    fun setPrevRandomRefreshEnabled(randomRefresh: Boolean) {
        prefs.putValue(PREV_RANDOM_REFRESH, randomRefresh)
    }

    fun getRandomRefreshLocation() =
        WallpaperLocation.fromId(prefs.getInt(RANDOM_REFRESH_LOCATION, 0))

    fun setRandomRefreshLocation(location: WallpaperLocation) =
        prefs.putValue(RANDOM_REFRESH_LOCATION, location.id)

    fun setRandomRefreshInterval(interval: RefreshInterval) {
        prefs.putValue(REFRESH_INTERVAL, interval.id)
    }

    fun getRandomRefreshInterval() =
        RefreshInterval.fromId(prefs.getInt(REFRESH_INTERVAL, RefreshInterval.ONE_H.id))


    fun setTheme(theme: Theme) {
        prefs.putValue(THEME, theme.id)
    }

    fun getTheme() = Theme.fromId(prefs.getInt(THEME, Theme.SYSTEM.id))

    fun setDefaultSort(sort: RWApi.Sort) {
        prefs.putValue(DEFAULT_SORT, sort.id)
    }

    fun getDefaultSort() = RWApi.Sort.fromId(prefs.getInt(DEFAULT_SORT, RWApi.Sort.HOT.id))

    fun clearRandomRefreshSettings() {
        prefs.edit().remove(REFRESH_INTERVAL).remove(RANDOM_REFRESH_LOCATION).remove(RANDOM_ORDER)
            .apply()
    }

    fun setColumnCount(count: ColumnCount) {
        prefs.putValue(COLUMN_COUNT, count.id)
    }

    fun getColumnCount() = ColumnCount.fromId(prefs.getInt(COLUMN_COUNT, 1))

    fun setAnimationsEnabled(animationsEnabled: Boolean) {
        prefs.putValue(ANIMATION_ENABLED, animationsEnabled)
    }

    fun getAnimationsEnabled() = prefs.getBoolean(ANIMATION_ENABLED, true)

    fun setNsfwAllowed(allow: Boolean) {
        prefs.putValue(ALLOW_NSFW, allow)
    }

    fun nsfwAllowed() = prefs.getBoolean(ALLOW_NSFW, false)

    fun setSwipeEnabled(swipeEnabled: Boolean) {
        prefs.putValue(SWIPE_ENABLED, swipeEnabled)
    }

    fun swipeEnabled() = prefs.getBoolean(SWIPE_ENABLED, false)
}

enum class Theme(override val id: Int, override val displayText: String, val mode: Int) :
    SettingsItem {
    LIGHT(0, "Light mode", AppCompatDelegate.MODE_NIGHT_NO),
    DARK(1, "Dark mode", AppCompatDelegate.MODE_NIGHT_YES),
    SYSTEM(2, "Follow system", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

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

enum class ColumnCount(
    override val id: Int,
    override val displayText: String,
    val count: Int,
    val imageHeightDp: Int = -1
) : SettingsItem {
    ONE(0, "One", 1, 420),
    TWO(1, "Two", 2, 340),
    THREE(2, "Three", 3, 220),
    FOUR(3, "Four", 4, 180);

    companion object {
        fun fromId(id: Int) = values().fromId(id, TWO)
    }
}

interface SettingsItem {
    val id: Int
    val displayText: String
}