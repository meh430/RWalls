package mp.redditwalls.composables.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(images, { it.networkId }) {
            ImageCard(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(275.dp),
                imageCardModel = it.toImageCardModel(
                    onLikeClick = {},
                    onClick = {},
                    onLongPress = {}
                )
            )
        }
    }
}