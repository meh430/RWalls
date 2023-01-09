package mp.redditwalls.preferences.enums

import mp.redditwalls.preferences.R

enum class ImageQuality(
    override val stringId: Int
) : PreferenceEnum {
    LOW(stringId = R.string.low_quality),
    MEDIUM(stringId = R.string.medium_quality),
    HIGH(stringId = R.string.high_quality)
}