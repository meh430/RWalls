package mp.redditwalls.design.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Image(
    modifier: Modifier = Modifier,
    imageUrl: String,
    showRipple: Boolean = true,
    onTap: () -> Unit = {},
    onDoubleTap: () -> Unit = {},
    onLongPress: () -> Unit = {}
) {

    SubcomposeAsyncImage(
        modifier = if (showRipple) {
            modifier
                .combinedClickable(
                    onClick = onTap,
                    onLongClick = onLongPress,
                    onDoubleClick = onDoubleTap
                )
        } else {
            modifier
                .combinedClickable(
                    onClick = onTap,
                    onLongClick = onLongPress,
                    onDoubleClick = onDoubleTap,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        },
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        loading = {
            Box(modifier = Modifier.size(32.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .align(
                            Alignment.Center
                        )
                )
            }
        },
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}