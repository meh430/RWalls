package mp.redditwalls.local.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import mp.redditwalls.local.daos.DbSubredditDao
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.models.DbSubreddit

class LocalSubredditsRepository @Inject constructor(private val dbSubredditDao: DbSubredditDao) {
    suspend fun insertDbSubreddit(dbSubreddit: DbSubreddit) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.insertDbSubreddit(dbSubreddit)
        }
    }

    suspend fun updateDbSubreddits(dbSubreddits: List<DbSubreddit>) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.updateDbSubreddits(dbSubreddits)
        }
    }

    suspend fun deleteDbSubreddit(dbImage: DbImage) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.deleteDbSubreddit(dbImage)
        }
    }

    fun getDbSubreddits(): Flow<List<DbSubreddit>> = dbSubredditDao.getDbSubreddits()
}