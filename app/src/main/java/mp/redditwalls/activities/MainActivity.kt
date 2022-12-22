package mp.redditwalls.activities

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.R
import mp.redditwalls.databinding.ActivityMainBinding
import mp.redditwalls.utils.matchDestination
import mp.redditwalls.viewmodels.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavBar.selectedItem = mainViewModel.selectedItem
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.bottomNavBar.apply {
            onSelected = { id, index ->
                mainViewModel.selectedItem = id
                selectedItem = id
                onNavDestinationSelected(navController, id, index)
            }
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.navigation_search_subreddits_screen) {
                    binding.bottomNavBar.isVisible = false
                } else {
                    binding.bottomNavBar.isVisible = true
                    mainViewModel.selectedItem = destination.id
                    selectedItem = destination.id
                }

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun onNavDestinationSelected(
        navController: NavController,
        id: Int,
        index: Int
    ): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (
            navController.currentDestination!!.parent!!.findNode(id)
                    is ActivityNavigator.Destination
        ) {
            builder.setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        } else {
            builder.setEnterAnim(R.animator.nav_default_enter_anim)
                .setExitAnim(R.animator.nav_default_exit_anim)
                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        }
        if (index and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = false,
                saveState = true
            )
        }
        val options = builder.build()
        return try {
            navController.navigate(id, null, options)
            navController.currentDestination?.matchDestination(id) == true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}