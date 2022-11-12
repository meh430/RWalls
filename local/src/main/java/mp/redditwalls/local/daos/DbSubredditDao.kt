package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbSubreddit

@Dao
interface DbSubredditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbSubreddit(dbSubreddit: DbSubreddit)

    @Update
    suspend fun updateDbSubreddits(dbSubreddits: List<DbSubreddit>)

    @Query("DELETE FROM SavedSubreddits WHERE name = :name")
    suspend fun deleteDbSubreddit(name: String)

    @Query("SELECT * FROM SavedSubreddits")
    fun getDbSubreddits(): Flow<List<DbSubreddit>>
}