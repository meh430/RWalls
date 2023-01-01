package mp.redditwalls.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.redditwalls.activities.SearchImagesActivity
import mp.redditwalls.activities.SearchImagesActivityArguments
import mp.redditwalls.models.RecentActivityItem

fun LazyListScope.recentActivityListItems(
    modifier: Modifier = Modifier,
    context: Context,
    recentActivityItems: List<RecentActivityItem>,
    onClick: (RecentActivityItem) -> Unit = { recentActivity ->
        when (recentActivity) {
            is RecentActivityItem.RefreshWallpaperActivityItem -> onImageCardClick(
                context,
                recentActivity.imageId
            )
            is RecentActivityItem.SearchAllActivityItem -> SearchImagesActivity.launch(
                context,
                SearchImagesActivityArguments(
                    query = recentActivity.query
                )
            )
            is RecentActivityItem.SearchSubredditActivityItem -> SearchImagesActivity.launch(
                context,
                SearchImagesActivityArguments(
                    query = recentActivity.query,
                    subreddit = recentActivity.subredditName
                )
            )
            is RecentActivityItem.SetWallpaperActivityItem -> onImageCardClick(
                context,
                recentActivity.imageId
            )
            is RecentActivityItem.VisitSubredditActivityItem -> SearchImagesActivity.launch(
                context,
                SearchImagesActivityArguments(
                    subreddit = recentActivity.subredditName
                )
            )
        }
    },
    onLongClick: (RecentActivityItem) -> Unit
) {
    items(recentActivityItems, { it.dbId }) { recentActivityItem ->
        RecentActivityCard(
            modifier = modifier,
            recentActivityItem = recentActivityItem,
            onClick = { onClick(recentActivityItem) },
            onLongClick = { onLongClick(recentActivityItem) }
        )
    }
    item("footer_spacer") {
        Spacer(modifier = Modifier.height(8.dp))
    }
}
