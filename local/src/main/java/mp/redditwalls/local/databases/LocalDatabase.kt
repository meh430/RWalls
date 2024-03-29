package mp.redditwalls.local.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mp.redditwalls.local.Constants.DEFAULT_SUBREDDIT
import mp.redditwalls.local.daos.DbImageDao
import mp.redditwalls.local.daos.DbImageFolderDao
import mp.redditwalls.local.daos.DbRecentActivityItemDao
import mp.redditwalls.local.daos.DbSubredditDao
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.models.DbImageFolder
import mp.redditwalls.local.models.DbImageFolder.Companion.DEFAULT_FOLDER_NAME
import mp.redditwalls.local.models.DbRecentActivityItem
import mp.redditwalls.local.models.DbSubreddit

@Database(
    entities = [
        DbImage::class,
        DbImageFolder::class,
        DbSubreddit::class,
        DbRecentActivityItem::class
    ],
    version = 1,
    exportSchema = true
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDbImageDao(): DbImageDao
    abstract fun getDbImageFolderDao(): DbImageFolderDao
    abstract fun getDbSubredditDao(): DbSubredditDao
    abstract fun getDbRecentActivityItemDao(): DbRecentActivityItemDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context) =
            INSTANCE ?: synchronized(this) {
                buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): LocalDatabase =
            Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                "RWallsDatabase"
            ).addCallback(getCallback(context)).build()

        private fun getCallback(context: Context) = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    getDatabase(context).run {
                        prePopulateSubredditDb(getDbSubredditDao())
                        prePopulateImageFolderDb(getDbImageFolderDao())
                    }
                }
            }
        }

        private suspend fun prePopulateSubredditDb(dbSubredditDao: DbSubredditDao) {
            dbSubredditDao.insertDbSubreddit(
                DbSubreddit(
                    name = DEFAULT_SUBREDDIT
                )
            )
        }

        private suspend fun prePopulateImageFolderDb(dbImageFolderDao: DbImageFolderDao) {
            dbImageFolderDao.insertDbImageFolder(
                DbImageFolder(
                    name = DEFAULT_FOLDER_NAME,
                    refreshEnabled = true
                )
            )
        }
    }
}