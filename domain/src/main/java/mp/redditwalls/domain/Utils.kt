package mp.redditwalls.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.ImageUrl
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
    ) = ImageUrl(
        url = when (this) {
            ImageQuality.LOW -> low
            ImageQuality.MEDIUM -> medium
            ImageQuality.HIGH -> high
        },
        lowQualityUrl = low,
        mediumQualityUrl = medium,
        highQualityUrl = high
    )

    fun <T1, T2, T3, T4, T5, T6, T7, T8, R : Any> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
    ): Flow<R> = combine(
        combine(flow, flow2, flow3, ::Triple),
        combine(flow4, flow5, flow6, ::Triple),
        combine(flow7, flow8, ::Pair)
    ) { t1, t2, t3 ->
        transform(
            t1.first,
            t1.second,
            t1.third,
            t2.first,
            t2.second,
            t2.third,
            t3.first,
            t3.second
        )
    }
}