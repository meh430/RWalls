package com.example.redditwalls.datasources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.redditwalls.models.Subreddit
import kotlinx.coroutines.flow.Flow

@Dao
interface SubredditsDAO {
    @Query("SELECT * FROM FavoriteSubreddits")
    fun getFavoriteSubredditsFlow(): Flow<List<Subreddit>>

    @Query("SELECT * FROM FavoriteSubreddits")
    suspend fun getFavoriteSubreddits(): List<Subreddit>

    @Query("DELETE FROM FavoriteSubreddits")
    suspend fun deleteAllFavorites()

    @Delete
    suspend fun deleteFavoriteSubreddit(subreddit: Subreddit)
}