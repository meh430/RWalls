package mp.redditwalls.preferences.enums

import mp.redditwalls.preferences.R

enum class DataSetting(override val stringId: Int): PreferenceEnum {
    WIFI(stringId = R.string.wifi_only),
    DATA(stringId = R.string.data_only),
    BOTH(stringId = R.string.wifi_and_data)
}