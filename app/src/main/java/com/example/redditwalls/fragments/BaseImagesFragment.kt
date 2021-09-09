package com.example.redditwalls.fragments

import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redditwalls.WallpaperHelper
import com.example.redditwalls.adapters.ImageClickListener
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.ColumnCount
import com.example.redditwalls.viewmodels.FavoritesViewModel
import com.example.redditwalls.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseImagesFragment : Fragment(), ImageClickListener {
    protected val favoritesViewModel: FavoritesViewModel by viewModels()
    protected val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    override fun onDoubleClick(image: Image) {
        lifecycleScope.launch {
            val added = favoritesViewModel.addFavorite(image)
            val msg = if (added) "Added to favorites" else "Removed from favorites"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLongClick(image: Image) {
        wallpaperHelper.showLocationPickerDialog(requireContext()) {
            lifecycleScope.launch {
                wallpaperHelper.setImageLinkAsWallpaper(requireContext(), image.imageLink, it)
            }
        }
    }

    fun getLayoutManager() = settingsViewModel.getColumnCount().let {
        if (it == ColumnCount.ONE) {
            LinearLayoutManager(requireContext())
        } else {
            GridLayoutManager(requireContext(), it.count)
        }
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.getAnimationsEnabled().also {
            if (it != settingsViewModel.animateTransition) {
                settingsViewModel.animateTransition = it
            }
        }
    }

    fun navigateToWall(imageView: View?, action: NavDirections) {
        if (imageView == null || !settingsViewModel.animateTransition) {
            findNavController().navigate(action)
        } else {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                androidx.core.util.Pair.create(imageView, "redditImage")
            )
            val extras = ActivityNavigatorExtras(options)
            findNavController().navigate(action, extras)
        }
    }
}