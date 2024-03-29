package mp.redditwalls.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mp.redditwalls.R
import mp.redditwalls.adapters.FavoritesAdapter
import mp.redditwalls.databinding.FragmentFavoritesBinding
import mp.redditwalls.utils.ImageLoader
import mp.redditwalls.utils.Utils
import mp.redditwalls.models.Image
import mp.redditwalls.viewmodels.BottomNavDestinations
import mp.redditwalls.viewmodels.MainViewModel


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

    private val favoritesAdapter: FavoritesAdapter by lazy {
        FavoritesAdapter(
            settingsViewModel.loadLowResPreviews(),
            settingsViewModel.getColumnCount(),
            this
        )
    }

    private val mainViewModel: MainViewModel by activityViewModels()

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
                            onClick(null, randomImage)
                        } else {
                            toaster.t("No favorites found :(")
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
            layoutManager = this@FavoritesFragment.getLayoutManager()
        }

        favoritesViewModel.getFavorites().observe(viewLifecycleOwner) {
            val hasFavs = it?.isNullOrEmpty() == false

            binding.imageScroll.isVisible = hasFavs
            binding.empty.empty.isVisible = !hasFavs

            if (hasFavs) {
                favoritesAdapter.submitList(it)
            }
        }

        mainViewModel.navIconClicked.observe(viewLifecycleOwner) {
            it?.takeIf { it.first == BottomNavDestinations.FAVORITES }?.let {
                binding.imageScroll.smoothScrollToPosition(0)
            }
        }
    }

    private fun deleteFavorites() {
        lifecycleScope.launch {
            val numFavs = favoritesViewModel.getFavoritesCount()
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

    override fun onClick(view: View?, image: Image) {
        val toWall =
            FavoritesFragmentDirections.actionNavigationFavoritesToNavigationWallpaper(
                image,
                null,
                null
            )
        navigateToWall(view, toWall, true)
    }
}