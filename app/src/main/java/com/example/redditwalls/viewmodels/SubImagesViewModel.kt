package com.example.redditwalls.viewmodels

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.redditwalls.datasources.RWApi.Sort
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.RWRepository
import com.example.redditwalls.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubImagesViewModel @Inject constructor(
    private val rwRepository: RWRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    private lateinit var subreddit: String
    var currentSort = settingsRepository.getDefaultSort()
    private var query = ""

    var columnCount = settingsRepository.getColumnCount()

    private val params: MutableLiveData<ImagesParams> = MutableLiveData()
    val imagePages: LiveData<PagingData<Image>> = Transformations.switchMap(params) {
        rwRepository.getImages(it.subreddit, it.query, it.sort)
    }.cachedIn(viewModelScope)

    fun setSort(sort: Sort) {
        currentSort = sort
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query,
            sort = sort
        )
    }

    fun setSubreddit(subreddit: String) {
        this.subreddit = subreddit
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query,
            sort = currentSort
        )
    }

    fun setQuery(query: String) {
        this.query = query
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query.takeIf { it.isNotBlank() } ?: "",
            sort = currentSort
        )
    }

    fun setSortAndQuery(sort: Sort, query: String = "") {
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query,
            sort = currentSort
        )
    }

    fun subredditHasChanged(subreddit: String): Boolean {
        return this.subreddit != subreddit
    }

    fun initialize(
        subreddit: String,
        sort: Sort = Sort.HOT,
        query: String = ""
    ) {
        this.subreddit = subreddit
        this.currentSort = sort
        this.query = query
        params.value = ImagesParams(
            subreddit = subreddit,
            sort = sort,
            query = query
        )
    }
}


data class ImagesParams(
    val subreddit: String = "",
    val query: String,
    val sort: Sort
)