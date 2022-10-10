package mp.redditwalls.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import mp.redditwalls.datasources.HistoryDAO
import mp.redditwalls.models.History

class HistoryRepository @Inject constructor(private val historyDAO: HistoryDAO) {
    suspend fun insertHistory(history: History) = withContext(Dispatchers.IO) {
        historyDAO.insertHistory(history)
    }

    suspend fun deleteHistory() = withContext(Dispatchers.IO) {
        historyDAO.deleteHistory()
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        historyDAO.deleteHistoryItem(id)
    }

    fun getHistoryFlow(): Flow<List<History>> = historyDAO.getHistoryFlow()

    suspend fun getHistory(): List<History> = withContext(Dispatchers.IO) {
        historyDAO.getHistory()
    }

    suspend fun getHistoryCount() = withContext(Dispatchers.IO) { historyDAO.getHistoryCount() }
}