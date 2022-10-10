package mp.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import mp.redditwalls.models.Resource
import mp.redditwalls.models.Subreddit
import mp.redditwalls.repositories.RWRepository

@HiltViewModel
class SearchSubViewModel @Inject constructor(
    private val rwRepository: RWRepository
) : BaseViewModel() {

    private val liveQuery = MutableLiveData("")

    @FlowPreview
    val queriedResults =
        liveQuery.asFlow()
            .debounce(300)
            .asLiveData(viewModelScope.coroutineContext).switchMap {
                val results = MutableLiveData<Resource<List<Subreddit>>>()
                getResource(results) {
                    rwRepository.searchSubs(it)
                }
                results
            }

    fun setQuery(query: String) {
        liveQuery.value = query
    }
}