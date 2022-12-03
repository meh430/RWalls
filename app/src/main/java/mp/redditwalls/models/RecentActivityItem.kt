package mp.redditwalls.models

import java.util.Date
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.local.enums.WallpaperLocation

sealed class RecentActivityItem(
    val dbId: Int,
    val createdAt: Date
) {
    class SearchSubredditActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val query: String
    ) : RecentActivityItem(dbId, createdAt)

    class SearchAllActivityItem(
        dbId: Int,
        createdAt: Date,
        val query: String
    ) : RecentActivityItem(dbId, createdAt)

    class VisitSubredditActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
    ) : RecentActivityItem(dbId, createdAt)

    class SetWallpaperActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val imageUrl: ImageUrl,
        val imageNetworkId: String,
        val wallpaperLocation: WallpaperLocation
    ) : RecentActivityItem(dbId, createdAt)

    class RefreshWallpaperActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val imageUrl: ImageUrl,
        val imageNetworkId: String,
        val wallpaperLocation: WallpaperLocation
    ) : RecentActivityItem(dbId, createdAt)
}

fun DomainRecentActivityItem.toRecentActivityItem() = when (this) {
    is DomainRecentActivityItem.DomainRefreshWallpaperActivityItem -> {
        RecentActivityItem.RefreshWallpaperActivityItem(
            dbId = dbId,
            createdAt = createdAt,
            subredditName = subredditName,
            imageUrl = domainImageUrl.toImageUrl(),
            imageNetworkId = imageNetworkId,
            wallpaperLocation = wallpaperLocation
        )
    }
    is DomainRecentActivityItem.DomainSearchAllActivityItem -> {
        RecentActivityItem.SearchAllActivityItem(
            dbId = dbId,
            createdAt = createdAt,
            query = query
        )
    }
    is DomainRecentActivityItem.DomainSearchSubredditActivityItem -> {
        RecentActivityItem.SearchSubredditActivityItem(
            dbId = dbId,
            createdAt = createdAt,
            query = query,
            subredditName = subredditName
        )
    }
    is DomainRecentActivityItem.DomainSetWallpaperActivityItem -> {
        RecentActivityItem.SetWallpaperActivityItem(
            dbId = dbId,
            createdAt = createdAt,
            subredditName = subredditName,
            imageUrl = domainImageUrl.toImageUrl(),
            imageNetworkId = imageNetworkId,
            wallpaperLocation = wallpaperLocation
        )
    }
    is DomainRecentActivityItem.DomainVisitSubredditActivityItem -> {
        RecentActivityItem.VisitSubredditActivityItem(
            dbId = dbId,
            createdAt = createdAt,
            subredditName = subredditName
        )
    }
}
