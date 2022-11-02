package mp.redditwalls.composables.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mp.redditwalls.design.components.ImageCard
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toImageCardModel

@Composable
fun ImagesList(
    modifier: Modifier,
    contentPadding: PaddingValues,
    images: List<ImageItemUiState>,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(images, { it.networkId }) {
            ImageCard(
                imageCardModel = it.toImageCardModel(
                    onLikeClick = {},
                    onClick = {},
                    onLongPress = {}
                )
            )
        }
    }
}