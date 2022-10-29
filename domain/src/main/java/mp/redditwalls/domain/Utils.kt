package mp.redditwalls.domain

import mp.redditwalls.network.models.TimeFilter
import mp.redditwalls.preferences.enums.ImageQuality
import mp.redditwalls.preferences.enums.SortOrder

internal object Utils {
    fun SortOrder.toTimeFilter() = when (this) {
        SortOrder.TOP_HOUR -> TimeFilter.HOUR
        SortOrder.TOP_DAY -> TimeFilter.DAY
        SortOrder.TOP_WEEK -> TimeFilter.WEEK
        SortOrder.TOP_MONTH -> TimeFilter.MONTH
        SortOrder.TOP_YEAR -> TimeFilter.YEAR
        else -> TimeFilter.ALL
    }

    fun ImageQuality.getImageUrl(low: String, medium: String, high: String) = when (this) {
        ImageQuality.LOW -> low
        ImageQuality.MEDIUM -> medium
        ImageQuality.HIGH -> high
    }
}