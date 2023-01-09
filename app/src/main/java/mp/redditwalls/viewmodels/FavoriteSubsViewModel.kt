package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.utils.removeSubPrefix
import mp.redditwalls.models.Subreddit
import mp.redditwalls.repositories.FavoriteSubredditsRepository
import mp.redditwalls.repositories.SettingsRepository

@HiltViewModel
class FavoriteSubsViewModel @Inject constructor(
    private val subsRepository: FavoriteSubredditsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun getFavoriteSubs() = subsRepository.getFavoriteSubredditsLiveData()

    suspend fun subExists(name: String) = subsRepository.isSaved(name)

    fun insertFavoriteSub(subreddit: Subreddit) {
        viewModelScope.launch {
            subsRepository.insertFavoriteSubreddit(
                subreddit.copy(
                    numSubscribers = "",
                    isSaved = true
                )
            )
            settingsRepository.setFeedURL(buildFeedURL())
        }
    }

    fun deleteFavoriteSub(subreddit: Subreddit) {
        viewModelScope.launch {
            subsRepository.deleteFavoriteSubreddit(subreddit.name)
            settingsRepository.setFeedURL(buildFeedURL())
        }
    }

    private suspend fun buildFeedURL() = subsRepository.getFavoriteSubreddits().let { favSubs ->
        if (favSubs.isNotEmpty()) {
            "r/" + favSubs.joinToString(separator = "+") { it.name.removeSubPrefix() }
        } else {
            SettingsRepository.FALLBACK_SUBREDDIT
        }
    }
}