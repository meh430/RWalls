package mp.redditwalls.local.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import mp.redditwalls.local.daos.DbSubredditDao
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

    suspend fun deleteDbSubreddit(id: Int) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.deleteDbSubreddit(id)
        }
    }

    fun getDbSubreddits(): Flow<List<DbSubreddit>> = dbSubredditDao.getDbSubreddits()
}