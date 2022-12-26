package mp.redditwalls.domain

import mp.redditwalls.domain.models.DomainImageUrl
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

    fun ImageQuality.getImageUrl(
        low: String, medium: String, high: String
    ) = DomainImageUrl(
        url = when (this) {
            ImageQuality.LOW -> low.ifEmpty { medium }.ifEmpty { high }
            ImageQuality.MEDIUM -> medium.ifEmpty { low }.ifEmpty { high }
            ImageQuality.HIGH -> high.ifEmpty { medium }.ifEmpty { low }
        },
        lowQualityUrl = low.ifEmpty { medium }.ifEmpty { high },
        mediumQualityUrl = medium.ifEmpty { low }.ifEmpty { high },
        highQualityUrl = high.ifEmpty { medium }.ifEmpty { low }
    )
}