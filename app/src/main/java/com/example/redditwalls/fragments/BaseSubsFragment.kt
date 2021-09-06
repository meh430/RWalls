package com.example.redditwalls.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redditwalls.adapters.SubredditsAdapter
import com.example.redditwalls.adapters.SubredditsAdapter.SubredditClickListener
import com.example.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions
import com.example.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.*
import com.example.redditwalls.datasources.RWApi.Companion.BASE
import com.example.redditwalls.misc.launchBrowser
import com.example.redditwalls.misc.removeSubPrefix
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.viewmodels.FavoriteSubsViewModel
import com.example.redditwalls.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

abstract class BaseSubsFragment : Fragment(), SubredditClickListener {
    protected val subsAdapter: SubredditsAdapter by lazy {
        SubredditsAdapter(this)
    }

    private val settingsViewModel: SettingsViewModel by viewModels()
    protected val favoriteSubsViewModel: FavoriteSubsViewModel by viewModels()

    override fun onMenuOpen(subreddit: Subreddit) {
        lifecycleScope.launch {
            val isSaved = favoriteSubsViewModel.subExists(subreddit.name)
            val items = SubredditMenuOptions.values().filter {
                val target = if (isSaved) {
                    FAVORITE
                } else {
                    UNFAVORITE
                }

                it != target
            }.map { it.displayText }.toTypedArray()
            MaterialAlertDialogBuilder(requireContext()).setTitle("Options")
                .setItems(items) { _, i ->
                    SubredditMenuOptions.fromText(items[i])?.let {
                        onMenuItemClick(subreddit, it)
                    }
                }.show()
        }
    }

    private fun onMenuItemClick(
        subreddit: Subreddit,
        option: SubredditMenuOptions
    ) {
        when (option) {
            DEFAULT -> settingsViewModel.setDefaultSub(subreddit.name)
            FAVORITE -> favoriteSubsViewModel.insertFavoriteSub(subreddit)
            UNFAVORITE -> favoriteSubsViewModel.deleteFavoriteSub(subreddit)
            LAUNCH_WEB -> "$BASE/r/${subreddit.name.removeSubPrefix()}".launchBrowser(
                requireActivity()
            )
            BROWSE -> onClick(subreddit)
        }
    }

    protected fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            adapter = subsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}