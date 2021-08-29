package com.example.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.redditwalls.models.Resource
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.repositories.FavoriteSubredditsRepository
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSubViewModel @Inject constructor(
    private val rwRepository: RWRepository,
    private val favoriteSubredditsRepository: FavoriteSubredditsRepository
) : BaseViewModel() {
    val searchResults = MutableLiveData<Resource<List<Subreddit>>>()

    init {
        searchResults.value = Resource.loading()
    }

    fun searchSubs(query: String) {
        getResource(searchResults) {
            rwRepository.searchSubs(query)
        }
    }

    fun getFavoriteSubs() = favoriteSubredditsRepository.getFavoriteSubredditsLiveData()

    fun insertFavoriteSub(subreddit: Subreddit) {
        viewModelScope.launch {
            favoriteSubredditsRepository.insertFavoriteSubreddit(
                subreddit.copy(
                    networkId = "",
                    numSubscribers = ""
                )
            )
        }
    }

    fun deleteFavoriteSub(subreddit: Subreddit) {
        viewModelScope.launch {
            favoriteSubredditsRepository.deleteFavoriteSubreddit(subreddit.id)
        }
    }
}