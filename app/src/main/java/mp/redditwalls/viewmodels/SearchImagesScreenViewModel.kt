package mp.redditwalls.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetImagesUseCase
import mp.redditwalls.models.SearchImagesScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemItemUiState
import mp.redditwalls.models.toSubredditItemUiState
import mp.redditwalls.preferences.enums.SortOrder

@HiltViewModel
class SearchImagesScreenViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
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
                                it.toImageItemItemUiState()
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
}