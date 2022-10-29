package mp.redditwalls.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import mp.redditwalls.utilities.toSeconds

@Entity(tableName = "SavedSubreddits")
data class DbSubreddit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val createdAt: Long = System.currentTimeMillis().toSeconds(),
    val name: String = ""
)