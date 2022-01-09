package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.redditwalls.models.History
import com.example.redditwalls.repositories.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val historyRepository: HistoryRepository) :
    ViewModel() {
    fun insertHistory(history: History) {
        viewModelScope.launch {
            historyRepository.insertHistory(history)
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            historyRepository.deleteHistory()
        }
    }

    suspend fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteHistoryItem(id)
        }
    }

    fun getHistory() =
        historyRepository.getHistoryFlow().asLiveData(viewModelScope.coroutineContext)
}