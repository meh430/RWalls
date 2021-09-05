package com.example.redditwalls

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.redditwalls.databinding.ActivityMainBinding
import com.example.redditwalls.repositories.SettingsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.elevation = 0.0F

        val navView: BottomNavigationView = binding.bottomNavView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorites,
                R.id.navigation_search,
                R.id.navigation_saved,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            binding.appbar.setExpanded(true, true)
            setToolbarSubtitle("")
            binding.bottomNavView.isVisible = destination.id != R.id.navigation_search_images

            if (destination.id == R.id.navigation_search_images) {
                val subreddit =
                    arguments?.getString("subreddit") ?: SettingsRepository.FALLBACK_SUBREDDIT

                setToolbarTitle(subreddit)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setToolbarTitle(title: String) {
        binding.toolbar.toolbar.title = title
    }

    fun setToolbarSubtitle(subtitle: String) {
        binding.toolbar.toolbar.subtitle = subtitle
    }
}