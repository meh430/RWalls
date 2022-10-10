package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.models.History
import mp.redditwalls.repositories.HistoryRepository

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

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteHistoryItem(id)
        }
    }

    suspend fun getHistoryCount() = historyRepository.getHistoryCount()

    fun getHistory() =
        historyRepository.getHistoryFlow().asLiveData(viewModelScope.coroutineContext)
}