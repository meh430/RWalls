package mp.redditwalls.design.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun Image(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDoubleTap: () -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                    onLongPress = { onLongPress() }
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