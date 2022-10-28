package mp.redditwalls.design.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import mp.redditwalls.design.RwTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubredditCard(
    modifier: Modifier = Modifier,
    subredditIconUrl: String,
    subredditName: String,
    subscriberCount: String,
    subredditDescription: String,
    isSaved: Boolean,
    onSaveChanged: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val outerPadding = 12.dp
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onDoubleTap = { onSaveChanged(!isSaved) }
                )
            },
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = outerPadding,
                        start = outerPadding
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubredditIcon(
                        subredditIconUrl = subredditIconUrl,
                    )
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            text = subredditName,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = subscriberCount,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                IconButton(onClick = { onSaveChanged(!isSaved) }) {
                    Icon(
                        imageVector = if (isSaved) {
                            Icons.Default.Bookmark
                        } else {
                            Icons.Default.BookmarkBorder
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }

            // description text
            Text(
                modifier = Modifier.padding(outerPadding),
                text = subredditDescription,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SubredditIcon(
    modifier: Modifier = Modifier,
    subredditIconUrl: String,
    size: Dp = 48.dp
) {
    Card(
        modifier = modifier.size(size),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(subredditIconUrl)
                .crossfade(true)
                .build(),
            loading = { CircularProgressIndicator() },
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
fun SubredditCardPreview() {
    var isSaved by remember { mutableStateOf(false) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
            ) {
                items(10) {
                    SubredditCard(
                        subredditIconUrl = "https://styles.redditmedia.com/t5_32ud3/styles/communityIcon_t1p7xfn28ly41.png?width=256&s=00087941365dfd6cf6ee80a2a339f962f27816ad",
                        subredditName = "r/wallpaper",
                        subscriberCount = "133k subscribers · 108 online",
                        subredditDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer accumsan, ante eu ullamcorper porttitor, dui dolor malesuada purus, sed fringilla mauris lorem quis augue. Proin commodo bibendum condimentum.",
                        isSaved = isSaved,
                        onSaveChanged = { isSaved = it },
                        onClick = {},
                        onLongPress = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}