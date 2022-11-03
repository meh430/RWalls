package mp.redditwalls.composables.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.composables.components.ImagesList
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.models.UiResult
import mp.redditwalls.viewmodels.HomeScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
) {
    val uiState = homeScreenViewModel.homeScreenUiState
    val uiResult = uiState.uiResult
    if (uiResult is UiResult.Error) {
        ErrorState(
            errorMessage = uiResult.errorMessage.orEmpty()
        ) {

        }
    } else {
        ImagesList(
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            images = homeScreenViewModel.homeScreenUiState.images,
            isLoading = homeScreenViewModel.homeScreenUiState.uiResult is UiResult.Loading
        )
    }
}