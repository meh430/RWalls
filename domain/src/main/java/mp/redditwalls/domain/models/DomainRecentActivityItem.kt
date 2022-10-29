package mp.redditwalls.domain.models

import java.util.Date
import mp.redditwalls.local.enums.WallpaperLocation

sealed class DomainRecentActivityItem(
    val dbId: Int,
    val createdAt: Date
) {
    class DomainSearchSubredditActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val query: String
    ) : DomainRecentActivityItem(dbId, createdAt)

    class DomainSearchAllActivityItem(
        dbId: Int,
        createdAt: Date,
        val query: String
    ) : DomainRecentActivityItem(dbId, createdAt)

    class DomainVisitSubredditActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
    ) : DomainRecentActivityItem(dbId, createdAt)

    class DomainSetWallpaperActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val imageUrl: String,
        val imageNetworkId: String,
        val wallpaperLocation: WallpaperLocation
    ) : DomainRecentActivityItem(dbId, createdAt)

    class DomainRefreshWallpaperActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val imageUrl: String,
        val imageNetworkId: String,
        val wallpaperLocation: WallpaperLocation
    ) : DomainRecentActivityItem(dbId, createdAt)
}