package com.example.redditwalls.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.redditwalls.R
import com.example.redditwalls.adapters.FavoritesAdapter
import com.example.redditwalls.databinding.FragmentFavoritesBinding
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.models.Image
import com.example.redditwalls.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FavoritesFragment : BaseImagesFragment() {

    companion object {
        const val FEELING_LUCKY = 0
        const val DOWNLOAD_ALL = 1
        const val DELETE_ALL = 2
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var imageLoader: ImageLoader

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val favoritesAdapter: FavoritesAdapter by lazy {
        FavoritesAdapter(
            settingsViewModel.loadLowResPreviews(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.favorites_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actions -> showActionsDialog()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showActionsDialog(): Boolean {

        val actions = listOf(
            "Feeling lucky" to R.drawable.ic_lucky,
            "Download all" to R.drawable.ic_download,
            "Delete all" to R.drawable.ic_delete
        )
        val actionsAdapter =
            object : ArrayAdapter<Pair<String, Int>>(
                requireContext(),
                android.R.layout.select_dialog_item,
                actions
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<View>(android.R.id.text1) as TextView

                    textView.textSize = 14f
                    textView.text = actions[position].first
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        actions[position].second,
                        0,
                        0,
                        0
                    )
                    textView.compoundDrawablePadding =
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            12f,
                            context.resources.displayMetrics
                        ).toInt()
                    return view
                }
            }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Actions")
            .setAdapter(actionsAdapter) { _, i ->
                when (i) {
                    FEELING_LUCKY -> lifecycleScope.launch(Dispatchers.Main) {
                        val randomImage = favoritesViewModel.getRandomFavoriteImage()
                        if (randomImage != null) {
                            onClick(randomImage)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No favorites found :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    DELETE_ALL -> deleteFavorites()
                    DOWNLOAD_ALL -> lifecycleScope.launch {
                        val favs = favoritesViewModel.getFavoritesAsList()
                        if (favs.isNotEmpty()) {
                            Utils.downloadAllImages(
                                requireContext(),
                                imageLoader,
                                favs
                            )
                        }
                    }
                }
            }.show()
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageScroll.apply {
            adapter = favoritesAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        favoritesViewModel.getFavorites().observe(viewLifecycleOwner) {
            val hasFavs = it?.isNullOrEmpty() == false

            binding.imageScroll.isVisible = hasFavs
            binding.empty.empty.isVisible = !hasFavs

            if (hasFavs) {
                favoritesAdapter.submitList(it)
            }
        }
    }

    fun deleteFavorites() {
        lifecycleScope.launch {
            val numFavs = favoritesViewModel.getFavoritesAsList().size
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete $numFavs saved favorites?")
                .setPositiveButton("Yes") { _, _ ->
                    favoritesViewModel.deleteAllFavorites()
                }
                .setNegativeButton("No") { _, _ -> }.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(image: Image) {
        val toWall =
            FavoritesFragmentDirections.actionNavigationFavoritesToNavigationWallpaper(image)
        findNavController().navigate(toWall)
    }
}