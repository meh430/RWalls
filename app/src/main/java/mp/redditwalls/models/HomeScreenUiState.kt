package mp.redditwalls.models

data class HomeScreenUiState(
    val images: List<ImageItemUiState> = emptyList(),
    val uiResult: UiResult = UiResult.Loading()
)
