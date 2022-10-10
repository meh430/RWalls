package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.databinding.FragmentHomeBinding
import mp.redditwalls.models.Image
import mp.redditwalls.viewmodels.BottomNavDestinations
import mp.redditwalls.viewmodels.MainViewModel

@AndroidEntryPoint
class HomeFragment : BaseApiImagesFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override val subreddit: String
        get() = settingsViewModel.getCurrentHome()

    override fun scrollToTop() {
        if (enableSwipe()) {
            binding.imagePager.currentItem = 0
        } else {
            binding.imageScroll.scrollToPosition(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImageViewer(binding.imageScroll, binding.imagePager)
        if (!enableSwipe()) {
            binding.swipeRefreshRecycler.isVisible = true
        }
        observeImages()
        binding.swipeRefreshRecycler.setOnRefreshListener {
            imagesAdapter.refresh()
            scrollToTop()
            binding.swipeRefreshRecycler.isRefreshing = false
        }
        addLoadStateListener(
            if (enableSwipe()) binding.imagePager else binding.imageScroll,
            binding.loading,
            binding.error,
            binding.empty
        )
        mainViewModel.navIconClicked.observe(viewLifecycleOwner) {
            it?.takeIf { it.first == BottomNavDestinations.HOME }
                ?.let {
                    if (enableSwipe()) {
                        imagesAdapter.refresh()
                        binding.imagePager.currentItem = 0
                    } else {
                        binding.imageScroll.smoothScrollToPosition(0)
                    }
                }
        }
    }

    override fun onClick(view: View?, image: Image) {
        val toWall =
            HomeFragmentDirections.actionNavigationHomeToNavigationWallpaper(image, null, null)
        navigateToWall(view, toWall)
    }

    override fun onResume() {
        super.onResume()

        val current = settingsViewModel.getCurrentHome()
        if (imagesViewModel.subredditHasChanged(current)) {
            imagesViewModel.setSubreddit(current)
        }

        // When column count is changed from settings, the recycler view needs to be re-rendered
        settingsViewModel.getColumnCount().takeIf { !enableSwipe() }?.also {
            if (it != imagesViewModel.columnCount) {
                imagesViewModel.columnCount = it
                imagesAdapter.setCount(it)
                binding.imageScroll.adapter = imagesAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickInfo(image: Image) {
        onClick(image = image, view = null)
    }

    override fun enableSwipe(): Boolean = settingsViewModel.swipeEnabled()
}