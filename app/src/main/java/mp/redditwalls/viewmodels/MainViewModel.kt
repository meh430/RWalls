package mp.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mp.redditwalls.R

// pass down bottom nav click events
class MainViewModel : ViewModel() {
    var selectedItem: Int = R.id.navigation_home_screen

    val navIconClicked = MutableLiveData<Pair<BottomNavDestinations, Boolean>>()

    fun onBottomNavItemClicked(id: Int) {
        val clickedItem = BottomNavDestinations.fromId(id)
        val refresh = navIconClicked.value?.second ?: false
        navIconClicked.value = clickedItem to !refresh
    }
}

enum class BottomNavDestinations {
    HOME,
    FAVORITES,
    SEARCH,
    SAVED_SUBS;

    companion object {
        fun fromId(id: Int) = when (id) {
            R.id.navigation_home -> HOME
            R.id.navigation_favorites -> FAVORITES
            R.id.navigation_search -> SEARCH
            R.id.navigation_saved -> SAVED_SUBS
            else -> HOME
        }
    }
}