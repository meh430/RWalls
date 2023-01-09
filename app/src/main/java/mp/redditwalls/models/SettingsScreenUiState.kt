package mp.redditwalls.models

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.R
import mp.redditwalls.preferences.PreferencesData
import mp.redditwalls.preferences.enums.DataSetting
import mp.redditwalls.preferences.enums.ImageQuality
import mp.redditwalls.preferences.enums.RefreshInterval
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.preferences.enums.Theme

data class SettingsScreenUiState(
    val defaultHomeSort: MutableState<SortOrder> = mutableStateOf(SortOrder.HOT),
    val previewResolution: MutableState<ImageQuality> = mutableStateOf(ImageQuality.HIGH),
    val theme: MutableState<Theme> = mutableStateOf(Theme.SYSTEM),
    val refreshEnabled: MutableState<Boolean> = mutableStateOf(true),
    val refreshInterval: MutableState<RefreshInterval> =
        mutableStateOf(RefreshInterval.TWENTY_FOUR_H),
    val dataSetting: MutableState<DataSetting> = mutableStateOf(DataSetting.BOTH),
    val verticalSwipeFeedEnabled: MutableState<Boolean> = mutableStateOf(false),
    val allowNsfw: MutableState<Boolean> = mutableStateOf(false),
    val currentRadioDialog: MutableState<SettingsRadioDialogModel?> = mutableStateOf(null),
    val refreshSettingsChanged: MutableState<Boolean> = mutableStateOf(false),
    val usePresetFolderWhenLiking: MutableState<Boolean> = mutableStateOf(false),
    val presetFolderName: MutableState<String> = mutableStateOf(""),
    val showImageFolderDialog: MutableState<Boolean> = mutableStateOf(false),
    val folderNames: SnapshotStateList<String> = mutableStateListOf()
)

sealed class SettingsRadioDialogModel(
    val titleId: Int,
    val options: List<Int>,
    val selection: Int
) {
    class PreviewResolutionDialog(selection: Int) : SettingsRadioDialogModel(
        titleId = R.string.image_preview_resolution,
        options = ImageQuality.values().map { it.stringId },
        selection = selection
    )

    class ThemeDialog(selection: Int) : SettingsRadioDialogModel(
        titleId = R.string.theme,
        options = Theme.values().map { it.stringId },
        selection = selection
    )

    class DefaultHomeScreenSortDialog(selection: Int) : SettingsRadioDialogModel(
        titleId = R.string.default_home_screen_sort,
        options = SortOrder.values().map { it.stringId },
        selection = selection
    )

    class RefreshIntervalDialog(selection: Int) : SettingsRadioDialogModel(
        titleId = R.string.refresh_interval,
        options = RefreshInterval.values().map { it.stringId },
        selection = selection
    )

    class DataSettingDialog(selection: Int) : SettingsRadioDialogModel(
        titleId = R.string.allow_refresh_when_on,
        options = DataSetting.values().map { it.stringId },
        selection = selection
    )

    fun changeSelection(selection: Int) = when (this) {
        is DataSettingDialog -> DataSettingDialog(selection)
        is DefaultHomeScreenSortDialog -> DefaultHomeScreenSortDialog(selection)
        is PreviewResolutionDialog -> PreviewResolutionDialog(selection)
        is RefreshIntervalDialog -> RefreshIntervalDialog(selection)
        is ThemeDialog -> ThemeDialog(selection).also {
            AppCompatDelegate.setDefaultNightMode(Theme.values()[selection].toThemeMode())
        }
    }
}

fun SettingsScreenUiState.updateState(data: PreferencesData) {
    defaultHomeSort.value = data.defaultHomeSort
    previewResolution.value = data.previewResolution
    theme.value = data.theme
    refreshEnabled.value = data.refreshEnabled
    refreshInterval.value = data.refreshInterval
    dataSetting.value = data.dataSetting
    verticalSwipeFeedEnabled.value = data.verticalSwipeFeedEnabled
    allowNsfw.value = data.allowNsfw
    usePresetFolderWhenLiking.value = data.usePresetFolderWhenLiking
    presetFolderName.value = data.presetFolderName
}

fun SettingsScreenUiState.getPreferenceData() = PreferencesData(
    defaultHomeSort = defaultHomeSort.value,
    previewResolution = previewResolution.value,
    theme = theme.value,
    refreshEnabled = refreshEnabled.value,
    refreshInterval = refreshInterval.value,
    dataSetting = dataSetting.value,
    verticalSwipeFeedEnabled = verticalSwipeFeedEnabled.value,
    allowNsfw = allowNsfw.value,
    usePresetFolderWhenLiking = usePresetFolderWhenLiking.value,
    presetFolderName = presetFolderName.value
)
