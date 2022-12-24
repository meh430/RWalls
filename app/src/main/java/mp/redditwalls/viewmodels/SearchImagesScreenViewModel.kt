package mp.redditwalls.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.AddRecentActivityItemUseCase
import mp.redditwalls.domain.usecases.GetImagesUseCase
import mp.redditwalls.models.SearchImagesScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemUiState
import mp.redditwalls.models.toSubredditItemUiState
import mp.redditwalls.preferences.enums.SortOrder

@HiltViewModel
class SearchImagesScreenViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val addRecentActivityItemUseCase: AddRecentActivityItemUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {
    val uiState = SearchImagesScreenUiState()
    val listState = LazyGridState()

    init {
        subscribeToImages()

        getImagesUseCase.init(viewModelScope)
        favoriteImageViewModel.init(viewModelScope)
        savedSubredditViewModel.init(viewModelScope)
    }

    fun onScreenLaunched(
        subreddit: String? = null,
        q: String? = null
    ) {
        uiState.apply {
            sortOrder.value = SortOrder.HOT
            subredditName.value = subreddit
            query.value = q.orEmpty()
        }
        fetchImages(true)
        addToSearchHistory(visit = true)
    }

    fun onQueryChanged(query: String) {
        uiState.query.value = query
    }

    fun setSortOrder(sortOrder: SortOrder) {
        uiState.sortOrder.value = sortOrder
        fetchImages(true)
    }

    fun fetchImages(reload: Boolean = false) {
        if (!reload && !uiState.hasMoreImages.value) {
            return
        }

        uiState.uiResult.value = UiResult.Loading()
        if (reload) {
            viewModelScope.launch {
                if (uiState.images.isNotEmpty()) {
                    listState.scrollToItem(0)
                }
                uiState.images.clear()
            }
        }

        viewModelScope.launch {
            getImagesUseCase(
                GetImagesUseCase.Params(
                    subreddit = uiState.subredditName.value,
                    query = uiState.query.value.takeIf { it.isNotBlank() },
                    sortOrder = uiState.sortOrder.value,
                    reload = reload
                )
            )
        }
        addToSearchHistory()
    }

    private fun subscribeToImages() {
        viewModelScope.launch {
            getImagesUseCase.sharedFlow.collect { result ->
                when (result) {
                    is DomainResult.Error ->
                        uiState.uiResult.value = UiResult.Error(result.message)
                    is DomainResult.Success -> uiState.apply {
                        uiState.uiResult.value = UiResult.Success()
                        images.addAll(
                            result.data?.images?.map {
                                it.toImageItemUiState()
                            }.orEmpty()
                        )
                        if (uiState.subredditItemUiState.value == null) {
                            uiState.subredditItemUiState.value =
                                result.data?.subreddit?.toSubredditItemUiState()
                        }
                        uiState.folderNames.clear()
                        uiState.folderNames.addAll(result.data?.folderNames.orEmpty())
                        uiState.hasMoreImages.value =
                            result.data?.nextPageId != null && uiState.images.isNotEmpty()
                    }
                }
            }
        }
    }

    private fun addToSearchHistory(visit: Boolean = false) {
        viewModelScope.launch {
            val query = uiState.query.value.takeIf { it.isNotBlank() }
            val subreddit = uiState.subredditName.value
            when {
                !subreddit.isNullOrBlank() && visit -> {
                    addRecentActivityItemUseCase(
                        DomainRecentActivityItem.DomainVisitSubredditActivityItem(
                            dbId = 0,
                            createdAt = Date(),
                            subredditName = subreddit,
                        )
                    )
                }
                !query.isNullOrBlank() && !subreddit.isNullOrBlank() -> {
                    addRecentActivityItemUseCase(
                        DomainRecentActivityItem.DomainSearchSubredditActivityItem(
                            dbId = 0,
                            createdAt = Date(),
                            subredditName = subreddit,
                            query = query
                        )
                    )
                }
                !query.isNullOrBlank() && !visit -> {
                    addRecentActivityItemUseCase(
                        DomainRecentActivityItem.DomainSearchAllActivityItem(
                            dbId = 0,
                            createdAt = Date(),
                            query = query
                        )
                    )
                }
            }
        }
    }
}