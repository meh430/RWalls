package mp.redditwalls.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import mp.redditwalls.utilities.toSeconds

@Entity(tableName = "SavedSubreddits")
data class DbSubreddit(
    @PrimaryKey
    val name: String,
    val createdAt: Long = System.currentTimeMillis().toSeconds(),
)