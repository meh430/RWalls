package mp.redditwalls.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import mp.redditwalls.local.models.DbImageFolder.Companion.DEFAULT_FOLDER_NAME
import mp.redditwalls.utilities.toSeconds


@Entity(tableName = "FavoriteImages")
data class DbImage(
    @PrimaryKey
    val id: String, // <network_id>@<index>
    val createdAt: Long = System.currentTimeMillis().toSeconds(),
    val postTitle: String = "",
    val subredditName: String = "",
    val postUrl: String = "",
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val sourceUrl: String = "",
    val imageFolderName: String = DEFAULT_FOLDER_NAME
)
