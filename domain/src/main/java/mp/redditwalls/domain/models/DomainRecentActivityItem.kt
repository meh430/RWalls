package mp.redditwalls.domain.models

import java.util.Date
import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.local.enums.RecentActivityType
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.models.DbRecentActivityItem
import mp.redditwalls.preferences.enums.ImageQuality

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
        val imageUrl: ImageUrl,
        val imageId: ImageId,
        val wallpaperLocation: WallpaperLocation
    ) : DomainRecentActivityItem(dbId, createdAt)

    class DomainRefreshWallpaperActivityItem(
        dbId: Int,
        createdAt: Date,
        val subredditName: String,
        val imageUrl: ImageUrl,
        val imageId: ImageId,
        val wallpaperLocation: WallpaperLocation
    ) : DomainRecentActivityItem(dbId, createdAt)
}

fun DbRecentActivityItem.toDomainRecentActivityItem(previewResolution: ImageQuality) =
    when (RecentActivityType.valueOf(activityType)) {
        RecentActivityType.SEARCH_SUB -> DomainRecentActivityItem.DomainSearchSubredditActivityItem(
            dbId = id,
            createdAt = Date(createdAt),
            subredditName = subredditName,
            query = query
        )
        RecentActivityType.SEARCH_ALL -> DomainRecentActivityItem.DomainSearchAllActivityItem(
            dbId = id,
            createdAt = Date(createdAt),
            query = query
        )
        RecentActivityType.VISIT_SUB -> DomainRecentActivityItem.DomainVisitSubredditActivityItem(
            dbId = id,
            createdAt = Date(createdAt),
            subredditName = subredditName
        )
        RecentActivityType.SET_WALLPAPER -> DomainRecentActivityItem.DomainSetWallpaperActivityItem(
            dbId = id,
            createdAt = Date(createdAt),
            subredditName = subredditName,
            imageUrl = previewResolution.getImageUrl(
                lowQualityUrl,
                mediumQualityUrl,
                sourceUrl
            ),
            imageId = ImageId(imageId),
            wallpaperLocation = WallpaperLocation.valueOf(wallpaperLocation)
        )
        RecentActivityType.REFRESH_WALLPAPER -> DomainRecentActivityItem.DomainRefreshWallpaperActivityItem(
            dbId = id,
            createdAt = Date(createdAt),
            subredditName = subredditName,
            imageUrl = previewResolution.getImageUrl(
                lowQualityUrl,
                mediumQualityUrl,
                sourceUrl
            ),
            imageId = ImageId(imageId),
            wallpaperLocation = WallpaperLocation.valueOf(wallpaperLocation)
        )
    }