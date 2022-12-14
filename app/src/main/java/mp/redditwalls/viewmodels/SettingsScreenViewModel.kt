package mp.redditwalls.viewmodels

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

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val getPreferencesUseCase: GetPreferencesUseCase,
    private val savePreferencesUseCase: SavePreferencesUseCase
) : ViewModel() {
    val settingsScreenUiState = SettingsScreenUiState()

    init {
        subscribeToPreferences()
        getPreferencesUseCase.init(viewModelScope)
    }

    fun savePreferences() {
        viewModelScope.launch {
            savePreferencesUseCase(settingsScreenUiState.getPreferenceData())
        }
    }

    fun changeDialogSelection(selection: Int) {
        settingsScreenUiState.currentRadioDialog.apply {
            value = value?.changeSelection(selection)
        }
    }

    fun showDialog(dialog: SettingsRadioDialogModel) {
        settingsScreenUiState.currentRadioDialog.value = dialog
    }

    fun dismissDialog() {
        settingsScreenUiState.currentRadioDialog.value = null
    }

    private fun subscribeToPreferences() {
        viewModelScope.launch {
            getPreferencesUseCase.sharedFlow.collect { result ->
                if (result is DomainResult.Success) {
                    result.data?.let { settingsScreenUiState.updateState(it) }
                }
            }
        }
    }
}