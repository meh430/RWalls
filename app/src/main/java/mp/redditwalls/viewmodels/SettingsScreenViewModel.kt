package mp.redditwalls.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetPreferencesUseCase
import mp.redditwalls.domain.usecases.SavePreferencesUseCase
import mp.redditwalls.models.SettingsRadioDialogModel
import mp.redditwalls.models.SettingsScreenUiState
import mp.redditwalls.models.getPreferenceData
import mp.redditwalls.models.updateState
import mp.redditwalls.preferences.enums.DataSetting
import mp.redditwalls.preferences.enums.ImageQuality
import mp.redditwalls.preferences.enums.RefreshInterval
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.preferences.enums.Theme

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val getPreferencesUseCase: GetPreferencesUseCase,
    private val savePreferencesUseCase: SavePreferencesUseCase
) : ViewModel() {
    val uiState = SettingsScreenUiState()

    init {
        subscribeToPreferences()
        getPreferencesUseCase.init(viewModelScope)
    }

    fun savePreferences() {
        viewModelScope.launch {
            savePreferencesUseCase(uiState.getPreferenceData())
        }
    }

    fun changeDialogSelection(selection: Int) {
        uiState.currentRadioDialog.apply {
            value = value?.changeSelection(selection)
        }
    }

    fun showDialog(dialog: SettingsRadioDialogModel) {
        uiState.currentRadioDialog.value = dialog
    }

    fun onDialogConfirmClicked() {
        uiState.currentRadioDialog.value?.let {
            uiState.apply {
                when (it) {
                    is SettingsRadioDialogModel.DataSettingDialog -> {
                        dataSetting.value = DataSetting.values()[it.selection]
                        refreshSettingsChanged.value = true
                    }
                    is SettingsRadioDialogModel.DefaultHomeScreenSortDialog -> {
                        defaultHomeSort.value = SortOrder.values()[it.selection]
                    }
                    is SettingsRadioDialogModel.PreviewResolutionDialog -> {
                        previewResolution.value = ImageQuality.values()[it.selection]
                    }
                    is SettingsRadioDialogModel.RefreshIntervalDialog -> {
                        refreshInterval.value = RefreshInterval.values()[it.selection]
                        refreshSettingsChanged.value = true
                    }
                    is SettingsRadioDialogModel.ThemeDialog -> {
                        theme.value = Theme.values()[it.selection]
                    }
                }
            }
            dismissDialog()
        }
    }

    fun dismissDialog() {
        uiState.currentRadioDialog.value?.let {
            if (it is SettingsRadioDialogModel.ThemeDialog) {
                AppCompatDelegate.setDefaultNightMode(uiState.theme.value.toThemeMode())
            }
        }
        uiState.currentRadioDialog.value = null
    }

    private fun subscribeToPreferences() {
        viewModelScope.launch {
            getPreferencesUseCase.sharedFlow.collect { result ->
                if (result is DomainResult.Success) {
                    result.data?.let { uiState.updateState(it) }
                }
            }
        }
    }
}