package com.example.redditwalls

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.redditwalls.datasources.FavoritesDAO
import com.example.redditwalls.datasources.HistoryDAO
import com.example.redditwalls.datasources.SubredditsDAO
import com.example.redditwalls.models.History
import com.example.redditwalls.models.Image
import com.example.redditwalls.models.Subreddit

@Database(
    entities = [Image::class, Subreddit::class, History::class],
    version = 4,
    exportSchema = true
)
abstract class RWDatabase : RoomDatabase() {

    abstract fun getFavoritesDAO(): FavoritesDAO
    abstract fun getSubredditDAO(): SubredditsDAO
    abstract fun getHistoryDAO(): HistoryDAO

    companion object {
        @Volatile
        private var INSTANCE: RWDatabase? = null

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `History` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `imageLink` TEXT NOT NULL, `postLink` TEXT NOT NULL, `subreddit` TEXT NOT NULL, `previewLink` TEXT NOT NULL, `dateCreated` INTEGER NOT NULL, `manuallySet` INTEGER NOT NULL, `location` INTEGER NOT NULL)")
            }
        }

        fun getDatabase(context: Context): RWDatabase =
            INSTANCE ?: synchronized(this) {
                buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, RWDatabase::class.java, "RedditWalls")
                .addMigrations(MIGRATION_3_4)
                .build()
    }
}