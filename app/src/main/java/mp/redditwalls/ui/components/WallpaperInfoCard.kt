package mp.redditwalls.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale
import mp.redditwalls.design.R
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.components.Chip
import mp.redditwalls.design.components.IconText
import mp.redditwalls.design.components.LikeButton
import mp.redditwalls.design.components.SubredditHeaderImage
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.SubredditItemUiState
import mp.redditwalls.utils.toFriendlyCount

@Composable
fun WallpaperInfoCard(
    modifier: Modifier = Modifier,
    image: ImageItemUiState,
    subreddit: SubredditItemUiState,
    expanded: Boolean,
    onExpand: () -> Unit,
    navigateToPost: () -> Unit,
    navigateToUser: () -> Unit,
    onLikeClick: (Boolean) -> Unit,
    onFolderNameClick: () -> Unit,
    onSaveClick: (Boolean) -> Unit,
    onSetWallpaperClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val df = remember { SimpleDateFormat("MMM. dd, yyyy", Locale.getDefault()) }
    val date = remember(image.createdAt) { df.format(image.createdAt) }
    val chips = remember(subreddit, image, date) {
        listOf(
            IconText(text = image.numUpvotes.toFriendlyCount(), icon = Icons.Default.ArrowUpward),
            IconText(text = image.numComments.toFriendlyCount(), icon = Icons.Default.Comment),
            IconText(text = date, icon = Icons.Default.CalendarMonth),
            IconText(text = "u/${image.author}", icon = Icons.Default.Person)
        ).zip(listOf(navigateToPost, navigateToPost, {}, navigateToUser))
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpand),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = image.postTitle,
                    maxLines = if (expanded) 8 else 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onExpand
                        ),
                    imageVector = if (expanded) {
                        Icons.Default.ExpandMore
                    } else {
                        Icons.Default.ExpandLess
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            ImageFolderButton(
                                modifier = Modifier.padding(end = 8.dp),
                                folderName = image.folderName,
                                onLikeClick = onLikeClick,
                                onFolderNameClick = onFolderNameClick
                            )
                        }
                        items(chips) { (chip, onChipClick) ->
                            Chip(
                                modifier = Modifier.padding(end = 8.dp),
                                text = chip.text,
                                icon = chip.icon,
                                onClick = onChipClick
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    SubredditHeaderImage(
                        headerImageUrl = subreddit.headerUrl,
                        iconImageUrl = subreddit.subredditIconUrl,
                        subredditName = "r/${subreddit.name}",
                        title = stringResource(
                            R.string.subscriber_count,
                            subreddit.numSubscribers.toFriendlyCount()
                        ),
                        isSaved = subreddit.isSaved.value,
                        onSaveChanged = onSaveClick
                    )
                    Spacer(Modifier.height(6.dp))
                    Row {
                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            onClick = onSetWallpaperClick,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Set Wallpaper")
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onDownloadClick,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(12.dp))
                                Text("Download")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageFolderButton(
    modifier: Modifier = Modifier,
    folderName: String,
    onLikeClick: (Boolean) -> Unit,
    onFolderNameClick: () -> Unit
) {
    val isLiked = folderName.isNotEmpty()
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .padding(vertical = 8.dp, horizontal = if (isLiked) 10.dp else 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = folderName.isNotEmpty()) {
                Row {
                    Text(
                        modifier = Modifier.clickable(onClick = onFolderNameClick),
                        text = folderName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            LikeButton(
                modifier = Modifier.size(18.dp),
                unlikedTint = MaterialTheme.colorScheme.onPrimary,
                isLiked = isLiked,
                onLikeClick = { onLikeClick(it) }
            )
        }
    }
}

@Preview
@Composable
fun WallpaperInfoCardPreview() {
    var expanded by remember { mutableStateOf(false) }
    RwTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            WallpaperInfoCard(
                modifier = Modifier.padding(12.dp),
                image = ImageItemUiState(
                    postTitle = "This is an image that is really cool wallpaper that I would totally want to set",
                    numUpvotes = 1230984,
                    numComments = 24457,
                    author = "anon"
                ),
                subreddit = SubredditItemUiState(
                    name = "MobileWallpaper",
                    numSubscribers = 247857,
                    subredditIconUrl = "https://512pixels.net/wp-content/uploads/2020/06/11-0-Color-Day-thumbnails-768x768.jpg",
                    headerUrl = "https://512pixels.net/downloads/macos-wallpapers-thumbs/10-12--thumb.jpg"
                ),
                expanded = expanded,
                onExpand = { expanded = !expanded },
                navigateToPost = {},
                navigateToUser = {},
                onLikeClick = {},
                onFolderNameClick = {},
                onSaveClick = {},
                onSetWallpaperClick = {},
                onDownloadClick = {}
            )
        }
    }
}
