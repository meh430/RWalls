package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.repositories.FavoriteSubredditsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteSubsViewModel @Inject constructor(
    private val subsRepository: FavoriteSubredditsRepository
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
        }
    }

    fun deleteFavoriteSub(subreddit: Subreddit) {
        viewModelScope.launch {
            subsRepository.deleteFavoriteSubreddit(subreddit.name)
        }
    }
}