package mp.redditwalls.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import mp.redditwalls.datasources.RWApi.Sort
import mp.redditwalls.models.Image
import mp.redditwalls.repositories.RWRepository
import mp.redditwalls.repositories.SettingsRepository

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