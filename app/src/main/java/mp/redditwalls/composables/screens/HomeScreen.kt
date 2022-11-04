package mp.redditwalls.composables.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.composables.components.ImagesList
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toDomainImage
import mp.redditwalls.viewmodels.HomeScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
) {
    val uiState = homeScreenViewModel.homeScreenUiState
    val uiResult = uiState.uiResult
    when {
        uiResult is UiResult.Error -> ErrorState(
            errorMessage = uiResult.errorMessage.orEmpty()
        ) {
            homeScreenViewModel.fetchHomeFeed(null)
        }
        uiResult is UiResult.Loading && uiState.images.isEmpty() -> Box {
            ThreeDotsLoader(modifier = Modifier.align(Alignment.Center))
        }
        else -> ImagesList(
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            images = uiState.images,
            isLoading = uiResult is UiResult.Loading,
            onLikeClick = { image, isLiked ->
                if (isLiked) {
                    homeScreenViewModel.addFavoriteImage(
                        domainImage = image.toDomainImage(),
                        refreshLocation = WallpaperLocation.BOTH
                    )
                } else {
                    homeScreenViewModel.removeFavoriteImage(image.dbId)
                }
            },
            onLoadMore = { homeScreenViewModel.fetchHomeFeed(null) }
        )
    }
}