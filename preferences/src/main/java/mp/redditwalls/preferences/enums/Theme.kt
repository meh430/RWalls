package mp.redditwalls.preferences.enums

import mp.redditwalls.preferences.R

enum class Theme(override val stringId: Int): PreferenceEnum {
    DARK(stringId = R.string.dark_theme),
    LIGHT(stringId = R.string.light_theme),
    SYSTEM(stringId = R.string.system_theme)
}