package mp.redditwalls.models

data class HomeScreenUiState(
    val images: List<ImageScreenState> = emptyList(),
    val uiResult: UiResult = UiResult.Loading(LoadingState.LOADING_FIRST)
)
