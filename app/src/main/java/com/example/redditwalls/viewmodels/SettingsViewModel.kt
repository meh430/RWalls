package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
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

    fun randomRefreshEnabled() = settingsRepository.randomRefreshEnabled()

    fun randomRefreshEnabled(enabled: Boolean) = settingsRepository.setRandomRefresh(enabled)


    fun getRandomRefreshPeriod() = settingsRepository.getRandomRefreshPeriod()

    fun setRandomRefreshPeriod(period: Int) = settingsRepository.setRandomRefreshPeriod(period)

    fun getRandomRefreshLocation() = settingsRepository.getRandomRefreshLocation()

    fun setRandomRefreshLocation(location: WallpaperLocation) =
        settingsRepository.setRandomRefreshLocation(location)
}
