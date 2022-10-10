package mp.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mp.redditwalls.models.Resource

open class BaseViewModel : ViewModel() {
    fun <T> getResource(liveData: MutableLiveData<Resource<T>>, getData: suspend () -> T) {
        liveData.value = Resource.loading()
        viewModelScope.launch {
            val result = try {
                Resource.success(getData())
            } catch (e: Exception) {
                Resource.error(e.message ?: "Failed to get data")
            }
            liveData.postValue(result)
        }
    }
}