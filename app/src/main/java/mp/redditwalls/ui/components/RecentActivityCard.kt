package mp.redditwalls.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import mp.redditwalls.design.components.ImageRecentActivityCard
import mp.redditwalls.design.components.TextRecentActivityCard
import mp.redditwalls.local.enums.getString
import mp.redditwalls.models.RecentActivityItem
import mp.redditwalls.utils.Utils

@Composable
fun RecentActivityCard(
    modifier: Modifier = Modifier,
    recentActivityItem: RecentActivityItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    recentActivityItem.let {
        when (it) {
            is RecentActivityItem.RefreshWallpaperActivityItem -> ImageRecentActivityCard(
                modifier = modifier,
                imageUrl = it.imageUrl.url,
                title = "Refreshed on ${
                    it.wallpaperLocation.getString(LocalContext.current).lowercase()
                }",
                subTitle = "r/${it.subredditName}",
                date = Utils.getFormattedDate(it.createdAt),
                onClick = onClick,
                onLongClick = onLongClick
            )
            is RecentActivityItem.SearchAllActivityItem -> TextRecentActivityCard(
                modifier = modifier,
                icon = Icons.Default.History,
                title = "Searched for images with '${it.query}'",
                onClick = onClick,
                onLongClick = onLongClick
            )
            is RecentActivityItem.SearchSubredditActivityItem -> TextRecentActivityCard(
                modifier = modifier,
                icon = Icons.Default.History,
                title = "Searched for images with '${it.query}'",
                subTitle = "in r/${it.subredditName}",
                onClick = onClick,
                onLongClick = onLongClick
            )
            is RecentActivityItem.SetWallpaperActivityItem -> ImageRecentActivityCard(
                modifier = modifier,
                imageUrl = it.imageUrl.url,
                title = "Set on ${
                    it.wallpaperLocation.getString(LocalContext.current).lowercase()
                }",
                subTitle = "r/${it.subredditName}",
                date = Utils.getFormattedDate(it.createdAt),
                onClick = onClick,
                onLongClick = onLongClick
            )
            is RecentActivityItem.VisitSubredditActivityItem -> TextRecentActivityCard(
                modifier = modifier,
                icon = Icons.Outlined.Explore,
                title = "Browsed images in r/${it.subredditName}",
                onClick = onClick,
                onLongClick = onLongClick
            )
        }
    }
}