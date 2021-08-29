package com.example.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.redditwalls.models.Resource
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchSubViewModel @Inject constructor(private val rwRepository: RWRepository) : BaseViewModel() {
    val searchResults = MutableLiveData<Resource<List<Subreddit>>>()

    init {
        searchResults.value = Resource.loading()
    }

    fun searchSubs(query: String) {
        getResource(searchResults) {
            rwRepository.searchSubs(query)
        }
    }
}