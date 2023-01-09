package mp.redditwalls.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Favorites")
data class Image(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var imageLink: String = "",
    var postLink: String = "",
    var subreddit: String = "",
    var previewLink: String = "",
    @Ignore var liked: Boolean = false
) : Parcelable
