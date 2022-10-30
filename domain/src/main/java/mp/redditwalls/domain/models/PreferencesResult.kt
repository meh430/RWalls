package mp.redditwalls.domain.models

import mp.redditwalls.preferences.enums.DataSetting
import mp.redditwalls.preferences.enums.ImageQuality
import mp.redditwalls.preferences.enums.RefreshInterval
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.preferences.enums.Theme

data class PreferencesResult(
    val defaultHomeSort: SortOrder = SortOrder.HOT,
    val previewResolution: ImageQuality = ImageQuality.HIGH,
    val theme: Theme = Theme.SYSTEM,
    val refreshEnabled: Boolean = true,
    val refreshInterval: RefreshInterval = RefreshInterval.TWENTY_FOUR_H,
    val dataSetting: DataSetting = DataSetting.BOTH,
    val verticalSwipeFeedEnabled: Boolean = false,
    val allowNsfw: Boolean = false
)