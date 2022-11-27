package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.usecases.GetDiscoverUseCase

@HiltViewModel
class DiscoverScreenViewModel @Inject constructor(
    private val getDiscoverUseCase: GetDiscoverUseCase,
    private val favoriteImageViewModelDelegate: FavoriteImageViewModelDelegate
) : FavoriteImageViewModel by favoriteImageViewModelDelegate, ViewModel() {
    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        subscribeToDiscoverFeed()
        getDiscoverUseCase.init(viewModelScope)
        getDiscoverFeed()
    }

    private fun getDiscoverFeed() {
        viewModelScope.launch {
            getDiscoverUseCase(Unit)
        }
    }

    private fun subscribeToDiscoverFeed() {
        viewModelScope.launch {
            getDiscoverUseCase.sharedFlow.collect {

            }
        }
    }
}