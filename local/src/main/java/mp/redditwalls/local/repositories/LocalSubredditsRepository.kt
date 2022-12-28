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

    suspend fun insertDbSubreddits(dbSubreddits: List<DbSubreddit>) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.insertDbSubreddits(dbSubreddits)
        }
    }

    suspend fun updateDbSubreddits(dbSubreddits: List<DbSubreddit>) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.updateDbSubreddits(dbSubreddits)
        }
    }

    suspend fun deleteDbSubreddit(name: String) {
        withContext(Dispatchers.IO) {
            dbSubredditDao.deleteDbSubreddit(name)
        }
    }

    suspend fun getDbSubredditsList(): List<DbSubreddit> = withContext(Dispatchers.IO) {
        dbSubredditDao.getDbSubredditsList()
    }

    fun getDbSubreddits(): Flow<List<DbSubreddit>> = dbSubredditDao.getDbSubreddits()
}