package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import com.example.redditwalls.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    fun getDefaultSub() = settingsRepository.getDefaultSub()
    fun setDefaultSub(subreddit: String) {
        val default = if (subreddit.startsWith("r/") && subreddit.length > 2) {
            subreddit.substring(2)
        } else {
            subreddit
        }

        settingsRepository.setDefaultSub(default)
    }

    fun loadLowResPreviews() = settingsRepository.loadLowResPreviews()
    fun setLoadLowResPreviews(loadLowRes: Boolean) =
        settingsRepository.setLoadLowResPreviews(loadLowRes)
}