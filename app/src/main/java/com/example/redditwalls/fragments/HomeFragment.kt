package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.redditwalls.databinding.FragmentHomeBinding
import com.example.redditwalls.models.Image
import com.example.redditwalls.viewmodels.BottomNavDestinations
import com.example.redditwalls.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseApiImagesFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override val subreddit: String
        get() = settingsViewModel.getCurrentHome()

    override fun scrollToTop() {
        binding.imageScroll.scrollToPosition(0)
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
        initRecyclerView(binding.imageScroll)
        observeImages()
        binding.swipeRefresh.setOnRefreshListener {
            imagesAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
        addLoadStateListener(
            binding.imageScroll,
            binding.loading,
            binding.error,
            binding.empty
        )
        mainViewModel.navIconClicked.observe(viewLifecycleOwner) {
            it?.takeIf { it.first == BottomNavDestinations.HOME }?.let {
                binding.imageScroll.smoothScrollToPosition(0)
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
        settingsViewModel.getColumnCount().also {
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
}