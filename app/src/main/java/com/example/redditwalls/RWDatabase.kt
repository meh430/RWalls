package com.example.redditwalls

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.redditwalls.datasources.FavoritesDAO
import com.example.redditwalls.datasources.SubredditsDAO
import com.example.redditwalls.models.Image
import com.example.redditwalls.models.Subreddit

@Database(entities = [Image::class, Subreddit::class], version = 1, exportSchema = false)
abstract class RWDatabase : RoomDatabase(){

    abstract fun getFavoritesDAO(): FavoritesDAO
    abstract fun getSubredditDAO(): SubredditsDAO

    companion object {
        @Volatile
        private var INSTANCE: RWDatabase? = null

        fun getDatabase(context: Context): RWDatabase =
            INSTANCE ?: synchronized(this) {
                buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, RWDatabase::class.java, "RedditWalls")
                .fallbackToDestructiveMigration()
                .build()
    }
}