package com.example.redditwalls.viewmodels

import androidx.lifecycle.*
import com.example.redditwalls.models.Resource
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@HiltViewModel
class SearchSubViewModel @Inject constructor(
    private val rwRepository: RWRepository
) : BaseViewModel() {

    private val liveQuery = MutableLiveData("")

    @FlowPreview
    val queriedResults =
        liveQuery.asFlow()
            .debounce(300)
            .asLiveData(viewModelScope.coroutineContext).switchMap {
                val results = MutableLiveData<Resource<List<Subreddit>>>()
                getResource(results) {
                    rwRepository.searchSubs(it)
                }
                results
            }

    fun setQuery(query: String) {
        liveQuery.value = query
    }
}