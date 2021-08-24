package com.example.redditwalls.datasources

import androidx.room.*
import com.example.redditwalls.models.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Image)

    @Query("DELETE FROM FAVORITES")
    suspend fun deleteAllFavorites()

    @Delete
    suspend fun deleteFavoriteImage(favorite: Image)

    @Query("SELECT * FROM Favorites")
    fun getFavoritesFlow(): Flow<List<Image>>

    @Query("SELECT * FROM Favorites")
    suspend fun getFavorites(): List<Image>

    @Query("SELECT EXISTS(SELECT * FROM Favorites WHERE imageLink = :imageLink)")
    suspend fun favoriteExists(imageLink: String): Boolean
}