package mp.redditwalls.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RecentActivity")
data class DbRecentActivityItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val activityType: String, // ActivityType
    val query: String = "", // empty if not search
    val subredditName: String = "",
    // wallpaper fields
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val sourceUrl: String = "",
    val imageNetworkId: String = "",
    val wallpaperLocation: String = "", // WallpaperLocation
)
