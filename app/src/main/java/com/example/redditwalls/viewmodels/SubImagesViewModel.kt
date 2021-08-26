package com.example.redditwalls.viewmodels

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.redditwalls.datasources.RWApi.Sort
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubImagesViewModel @Inject constructor(private val rwRepository: RWRepository) : ViewModel() {
    private lateinit var subreddit: String
    private var sort: Sort = Sort.HOT
    private var query = ""

    private val params: MutableLiveData<ImagesParams> = MutableLiveData()
    val imagePages: LiveData<PagingData<Image>> = Transformations.switchMap(params) {
        rwRepository.getImages(it.subreddit, it.query, it.sort)
    }.cachedIn(viewModelScope)

    fun getImages(subreddit: String, query: String = "", sort: Sort = Sort.HOT) =
        rwRepository.getImages(subreddit, query, sort).cachedIn(viewModelScope)

    fun setSort(sort: Sort) {
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query,
            sort = sort
        )
    }

    fun setQuery(query: String) {
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query.takeIf { it.isNotBlank() } ?: "",
            sort = sort
        )
    }

    fun setSortAndQuery(sort: Sort, query: String = "") {
        params.value = ImagesParams(
            subreddit = subreddit,
            query = query,
            sort = sort
        )
    }

    fun initialize(subreddit: String, sort: Sort = Sort.HOT, query: String = "") {
        this.subreddit = subreddit
        this.sort = sort
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