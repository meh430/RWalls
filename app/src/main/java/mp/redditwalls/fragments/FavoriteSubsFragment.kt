package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.databinding.FragmentFavoriteSubsBinding
import mp.redditwalls.models.Subreddit
import mp.redditwalls.viewmodels.BottomNavDestinations
import mp.redditwalls.viewmodels.MainViewModel

@AndroidEntryPoint
class FavoriteSubsFragment : BaseSubsFragment() {

    private var _binding: FragmentFavoriteSubsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteSubsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView(binding.subScroll)
        observeFavorites()
        mainViewModel.navIconClicked.observe(viewLifecycleOwner) {
            it?.takeIf { it.first == BottomNavDestinations.SAVED_SUBS }?.let {
                binding.subScroll.smoothScrollToPosition(0)
            }
        }
    }

    private fun observeFavorites() {
        favoriteSubsViewModel.getFavoriteSubs().observe(viewLifecycleOwner) {
            binding.loading.isVisible = false
            binding.subScroll.isVisible = false
            binding.empty.empty.isVisible = false
            if (it.isNullOrEmpty()) {
                binding.empty.empty.isVisible = true
            } else {
                binding.subScroll.isVisible = true
                subsAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(subreddit: Subreddit) {
        val toImages =
            FavoriteSubsFragmentDirections.actionNavigationSavedToSearchImagesFragment(subreddit.name)

        findNavController().navigate(toImages)
    }
}