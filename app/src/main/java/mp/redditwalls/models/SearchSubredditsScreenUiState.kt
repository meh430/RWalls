package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class SearchSubredditsScreenUiState(
    val query: MutableState<String> = mutableStateOf("")
)