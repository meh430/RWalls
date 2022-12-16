package mp.redditwalls.preferences.enums

import androidx.appcompat.app.AppCompatDelegate
import mp.redditwalls.preferences.R

enum class Theme(override val stringId: Int): PreferenceEnum {
    DARK(stringId = R.string.dark_theme),
    LIGHT(stringId = R.string.light_theme),
    SYSTEM(stringId = R.string.system_theme);

    fun toThemeMode() = when (this) {
        DARK -> AppCompatDelegate.MODE_NIGHT_YES
        LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
}