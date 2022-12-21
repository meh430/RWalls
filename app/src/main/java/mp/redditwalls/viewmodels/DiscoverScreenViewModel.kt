package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetDiscoverUseCase
import mp.redditwalls.models.DiscoverScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.clear
import mp.redditwalls.models.toRecentActivityItem
import mp.redditwalls.models.toRecommendedSubredditUiState

@HiltViewModel
class DiscoverScreenViewModel @Inject constructor(
    private val getDiscoverUseCase: GetDiscoverUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {

    val uiState = DiscoverScreenUiState()

    init {
        favoriteImageViewModel.init(viewModelScope)
        savedSubredditViewModel.init(viewModelScope)
        subscribeToDiscoverFeed()
        getDiscoverUseCase.init(viewModelScope)
        getDiscoverFeed()
    }

    fun getDiscoverFeed() {
        viewModelScope.launch {
            getDiscoverUseCase(Unit)
        }
    }

    private fun subscribeToDiscoverFeed() {
        viewModelScope.launch {
            getDiscoverUseCase.sharedFlow.collect {
                when (it) {
                    is DomainResult.Error -> {
                        uiState.uiResult.value = UiResult.Error(it.message)
                    }
                    is DomainResult.Success -> {
                        uiState.apply {
                            clear()
                            uiResult.value = UiResult.Success()
                            allowNsfw.value = it.data?.allowNsfw == true
                            usePresetFolderWhenLiking.value =
                                it.data?.usePresetFolderWhenLiking == true
                            recommendedSubreddits.addAll(
                                it.data?.recommendations?.map { recommendation ->
                                    recommendation.toRecommendedSubredditUiState()
                                }.orEmpty()
                            )
                            recentActivityItems.addAll(
                                it.data?.mostRecentActivities?.map { activity ->
                                    activity.toRecentActivityItem()
                                }.orEmpty()
                            )
                            folderNames.addAll(it.data?.folderNames.orEmpty())
                        }
                    }
                }
            }
        }
    }
}