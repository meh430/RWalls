package mp.redditwalls.preferences.enums

import java.util.concurrent.TimeUnit
import mp.redditwalls.preferences.R

enum class RefreshInterval(override val stringId: Int): PreferenceEnum {
    FIFTEEN_M(stringId = R.string.fifteen_minutes),
    THIRTY_M(stringId = R.string.thirty_minutes),
    ONE_H(stringId = R.string.one_hour),
    SIX_H(stringId = R.string.six_hours),
    TWELVE_H(stringId = R.string.twelve_hours),
    TWENTY_FOUR_H(stringId = R.string.twenty_four_hours);

    fun getAmount(): Long = when (this) {
        FIFTEEN_M -> 15
        THIRTY_M -> 30
        ONE_H -> 1
        SIX_H -> 6
        TWELVE_H -> 12
        TWENTY_FOUR_H -> 24
    }

    fun getTimeUnit() = when (this) {
        FIFTEEN_M -> TimeUnit.MINUTES
        THIRTY_M -> TimeUnit.MINUTES
        ONE_H -> TimeUnit.HOURS
        SIX_H -> TimeUnit.HOURS
        TWELVE_H -> TimeUnit.HOURS
        TWENTY_FOUR_H -> TimeUnit.HOURS
    }
}