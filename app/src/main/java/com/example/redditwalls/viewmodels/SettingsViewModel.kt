package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.misc.removeSubPrefix
import com.example.redditwalls.repositories.ColumnCount
import com.example.redditwalls.repositories.RefreshInterval
import com.example.redditwalls.repositories.SettingsRepository
import com.example.redditwalls.repositories.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    fun getDefaultSub() = settingsRepository.getDefaultSub()
    fun setDefaultSub(subreddit: String) {
        val default = subreddit.removeSubPrefix()

        settingsRepository.setDefaultSub(default)
    }

    fun loadLowResPreviews() = settingsRepository.loadLowResPreviews()
    fun setLoadLowResPreviews(loadLowRes: Boolean) =
        settingsRepository.setLoadLowResPreviews(loadLowRes)

    fun randomRefreshEnabled() = settingsRepository.randomRefreshEnabled()

    fun setRandomRefresh(enabled: Boolean) = settingsRepository.setRandomRefresh(enabled)

    fun getRandomRefreshInterval() = settingsRepository.getRandomRefreshInterval()

    fun setRandomRefreshInterval(refreshInterval: RefreshInterval) =
        settingsRepository.setRandomRefreshInterval(refreshInterval)

    fun getRandomRefreshLocation() = settingsRepository.getRandomRefreshLocation()

    fun setRandomRefreshLocation(location: WallpaperLocation) =
        settingsRepository.setRandomRefreshLocation(location)

    fun randomRefreshSettingsChanged(
        interval: RefreshInterval,
        location: WallpaperLocation
    ): Boolean {
        val savedInterval = getRandomRefreshInterval()
        val refreshChanged = interval != savedInterval

        val savedLocation = getRandomRefreshLocation()
        val locationChanged = location != savedLocation

        return refreshChanged || locationChanged
    }

    fun setTheme(theme: Theme) {
        settingsRepository.setTheme(theme)
    }

    fun getTheme() = settingsRepository.getTheme()

    fun setDefaultSort(sort: RWApi.Sort) {
        settingsRepository.setDefaultSort(sort)
    }

    fun getDefaultSort() = settingsRepository.getDefaultSort()

    fun clearRandomRefreshSettings() = settingsRepository.clearRandomRefreshSettings()

    fun setColumnCount(count: ColumnCount) = settingsRepository.setColumnCount(count)

    fun getColumnCount() = settingsRepository.getColumnCount()
}
