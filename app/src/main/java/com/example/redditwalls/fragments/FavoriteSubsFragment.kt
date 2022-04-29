package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.redditwalls.R
import com.example.redditwalls.databinding.FragmentFavoriteSubsBinding
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.viewmodels.BottomNavDestinations
import com.example.redditwalls.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> showDeleteSubsDialog().let { true }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun showDeleteSubsDialog() {
        lifecycleScope.launch {
            val count = favoriteSubsViewModel.getFavoriteSubsCount()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete $count saved subreddits?")
                .setPositiveButton("Yes") { _, _ ->
                    favoriteSubsViewModel.deleteFavoriteSubs()
                }
                .setNegativeButton("No") { _, _ -> }.show()
        }
    }
}