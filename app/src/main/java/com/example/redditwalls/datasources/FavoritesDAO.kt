package com.example.redditwalls.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.redditwalls.models.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Image)

    @Query("DELETE FROM FAVORITES")
    suspend fun deleteAllFavorites()

    @Query("DELETE FROM FAVORITES WHERE imageLink = :imageLink")
    suspend fun deleteFavoriteImage(imageLink: String)

    @Query("SELECT * FROM Favorites")
    fun getFavoritesFlow(): Flow<List<Image>>

    @Query("SELECT * FROM Favorites")
    suspend fun getFavorites(): List<Image>

    @Query("SELECT EXISTS(SELECT * FROM Favorites WHERE imageLink = :imageLink)")
    suspend fun favoriteExists(imageLink: String): Boolean

    @Query("SELECT * FROM Favorites ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomFavoriteImage(): Image
}