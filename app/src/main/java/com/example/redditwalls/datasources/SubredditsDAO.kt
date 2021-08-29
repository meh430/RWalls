package com.example.redditwalls.datasources

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.redditwalls.models.Subreddit

@Dao
interface SubredditsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteSubreddit(favorite: Subreddit)

    @Query("SELECT * FROM FavoriteSubreddits")
    fun getFavoriteSubredditsLiveData(): LiveData<List<Subreddit>>

    @Query("SELECT * FROM FavoriteSubreddits")
    suspend fun getFavoriteSubreddits(): List<Subreddit>

    @Query("DELETE FROM FavoriteSubreddits")
    suspend fun deleteAllFavorites()

    @Query("DELETE FROM FavoriteSubreddits WHERE name = :name")
    suspend fun deleteFavoriteSubreddit(name: String)
}