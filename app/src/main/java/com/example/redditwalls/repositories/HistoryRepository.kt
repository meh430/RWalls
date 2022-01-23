package com.example.redditwalls.repositories

import com.example.redditwalls.datasources.HistoryDAO
import com.example.redditwalls.models.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

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