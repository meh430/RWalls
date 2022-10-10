package mp.redditwalls.preferences.enums

import mp.redditwalls.preferences.R

enum class RefreshInterval(override val stringId: Int): PreferenceEnum {
    FIFTEEN_M(stringId = R.string.fifteen_minutes),
    THIRTY_M(stringId = R.string.thirty_minutes),
    ONE_H(stringId = R.string.one_hour),
    SIX_H(stringId = R.string.six_hours),
    TWELVE_H(stringId = R.string.twelve_hours),
    TWENTY_FOUR_H(stringId = R.string.twenty_four_hours)
}