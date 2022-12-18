package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetImagesUseCase
import mp.redditwalls.models.SearchImagesScreenUiState

class SearchImagesScreenViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {
    val uiState = SearchImagesScreenUiState()

    init {
        subscribeToImages()

        getImagesUseCase.init(viewModelScope)
        favoriteImageViewModel.init(viewModelScope)
        savedSubredditViewModel.init(viewModelScope)
    }

    private fun subscribeToImages() {
        viewModelScope.launch {
            getImagesUseCase.sharedFlow.collect {
                when (it) {
                    is DomainResult.Error -> TODO()
                    is DomainResult.Success -> TODO()
                }
            }
        }
    }
}