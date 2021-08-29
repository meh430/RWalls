package com.example.redditwalls.datasources

import androidx.room.*
import com.example.redditwalls.models.Subreddit
import kotlinx.coroutines.flow.Flow

@Dao
interface SubredditsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteSubreddit(favorite: Subreddit)

    @Query("SELECT * FROM FavoriteSubreddits")
    fun getFavoriteSubredditsFlow(): Flow<List<Subreddit>>

    @Query("SELECT * FROM FavoriteSubreddits")
    suspend fun getFavoriteSubreddits(): List<Subreddit>

    @Query("DELETE FROM FavoriteSubreddits")
    suspend fun deleteAllFavorites()

    @Delete
    suspend fun deleteFavoriteSubreddit(subreddit: Subreddit)
}