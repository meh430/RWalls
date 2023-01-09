package mp.redditwalls.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val imageLink: String = "",
    val postLink: String = "",
    val subreddit: String = "",
    val previewLink: String = "",
    val dateCreated: Long = 0L,
    val manuallySet: Boolean = true,
    val location: Int = 0
) {
    constructor(
        image: Image,
        dateCreated: Long,
        manuallySet: Boolean = true,
        location: Int = 0
    ) : this(
        imageLink = image.imageLink,
        postLink = image.postLink,
        subreddit = image.subreddit,
        previewLink = image.previewLink,
        dateCreated = dateCreated,
        manuallySet = manuallySet,
        location = location
    )

    fun toImage(): Image = Image(
        imageLink = imageLink,
        postLink = postLink,
        subreddit = subreddit,
        previewLink = previewLink
    )
}