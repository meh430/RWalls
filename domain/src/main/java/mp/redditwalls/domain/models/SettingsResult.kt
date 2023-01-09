package mp.redditwalls.domain.models

import mp.redditwalls.preferences.PreferencesData

data class SettingsResult(
    val preferencesData: PreferencesData = PreferencesData(),
    val folderNames: List<String> = emptyList()
)
