package mp.redditwalls.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.RwTheme

@Composable
fun DiscoverSubredditCard(
    modifier: Modifier = Modifier,
    subredditIconUrl: String,
    subredditName: String,
    subscriberCount: String,
    isSaved: Boolean,
    imageCardModels: List<ImageCardModel>,
    onSaveChanged: (Boolean) -> Unit,
) {
    val outerPadding = 16.dp
    Column(modifier = modifier) {
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
            SaveButton(isSaved = isSaved, onClick = onSaveChanged)
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(imageCardModels, { it.key }) { imageCardModel ->
                ImageCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(250.dp)
                        .width(160.dp),
                    imageCardModel = imageCardModel
                )
            }
        }
    }
}

@Preview
@Composable
fun DiscoverSubredditCardPreview() {
    var isSaved by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn {
                items(4) {
                    DiscoverSubredditCard(
                        subredditIconUrl = "https://styles.redditmedia.com/t5_32ud3/styles/communityIcon_t1p7xfn28ly41.png?width=256&s=00087941365dfd6cf6ee80a2a339f962f27816ad",
                        subredditName = "r/wallpaper",
                        subscriberCount = "133k subscribers",
                        isSaved = isSaved,
                        imageCardModels = List(25) {
                            ImageCardModel(
                                imageUrl = "https://c4.wallpaperflare.com/wallpaper/165/600/954/iphone-ios-ipad-ipod-wallpaper-preview.jpg",
                                title = "Cool Wallpaper",
                                subTitle = "r/wallpaper",
                                isAlbum = true,
                                isLiked = isLiked,
                                onLikeClick = { newValue ->
                                    isLiked = newValue
                                },
                                onClick = {},
                                onLongPress = {}
                            )
                        },
                        onSaveChanged = { isSaved = it }
                    )
                }
            }
        }

    }
}