package mp.redditwalls.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.utilities.toSeconds


@Entity(tableName = "FavoriteImages")
data class DbImage(
    @PrimaryKey
    val networkId: String,
    val createdAt: Long = System.currentTimeMillis().toSeconds(),
    val postTitle: String = "",
    val subredditName: String = "",
    val postUrl: String = "",
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val sourceUrl: String = "",
    val refreshLocation: String = WallpaperLocation.BOTH.name
)
