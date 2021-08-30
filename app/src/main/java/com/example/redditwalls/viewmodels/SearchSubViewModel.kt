package com.example.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.redditwalls.models.Resource
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.repositories.FavoriteSubredditsRepository
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchSubViewModel @Inject constructor(
    private val rwRepository: RWRepository,
    private val subsRepository: FavoriteSubredditsRepository
) : BaseViewModel() {

    val searchResults = MutableLiveData<Resource<List<Subreddit>>>()
    private var currentQuery = ""

    init {
        searchResults.value = Resource.success(emptyList())
    }

    fun searchSubs(query: String) {
        currentQuery = query
        getResource(searchResults) {
            checkIfSubsAreSaved(rwRepository.searchSubs(query))
        }
    }

    fun refresh() {
        getResource(searchResults) {
            searchResults.value?.data?.let {
                checkIfSubsAreSaved(it)
            } ?: emptyList()
        }
    }

    private suspend fun checkIfSubsAreSaved(subs: List<Subreddit>) = subs.map {
        it.copy(isSaved = subsRepository.isSaved(it.name))
    }
}