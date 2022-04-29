package com.example.redditwalls.fragments

import android.view.View
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
import com.example.redditwalls.adapters.ImagePageListener
import com.example.redditwalls.misc.Toaster
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.ColumnCount
import com.example.redditwalls.viewmodels.FavoritesViewModel
import com.example.redditwalls.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseImagesFragment : Fragment(), ImagePageListener {
    protected val favoritesViewModel: FavoritesViewModel by viewModels()
    protected val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    @Inject
    lateinit var toaster: Toaster

    override fun onDoubleClick(image: Image) {
        lifecycleScope.launch {
            val added = favoritesViewModel.addFavorite(image)
            val msg = if (added) "Added to favorites" else "Removed from favorites"

            toaster.t(msg, force = true)
        }
    }

    override fun onLongClick(image: Image) {
        wallpaperHelper.showLocationPickerDialog(requireContext()) {
            lifecycleScope.launch {
                wallpaperHelper.setImageAsWallpaper(requireContext(), image, it)
            }
        }
    }

    override fun onSetWallpaper(image: Image) {
        onLongClick(image)
    }

    override fun onClickInfo(image: Image) {}

    override fun onLike(image: Image) {}

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

    fun navigateToWall(
        imageView: View?,
        action: NavDirections,
        turnOffAnimations: Boolean = false
    ) {
        if (imageView == null || !settingsViewModel.animateTransition || turnOffAnimations) {
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