package com.example.redditwalls.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redditwalls.adapters.SubredditsAdapter
import com.example.redditwalls.adapters.SubredditsAdapter.SubredditClickListener
import com.example.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions
import com.example.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.*
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.viewmodels.FavoriteSubsViewModel
import com.example.redditwalls.viewmodels.SettingsViewModel

abstract class BaseSubsFragment : Fragment(), SubredditClickListener {
    protected val subsAdapter: SubredditsAdapter by lazy {
        SubredditsAdapter(this)
    }

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val favoriteSubsViewModel: FavoriteSubsViewModel by viewModels()

    override fun onMenuItemClick(
        subreddit: Subreddit,
        option: SubredditMenuOptions
    ) {
        when (option) {
            DEFAULT -> settingsViewModel.setDefaultSub(subreddit.name)
            FAVORITE -> favoriteSubsViewModel.insertFavoriteSub(subreddit)
            UNFAVORITE -> favoriteSubsViewModel.deleteFavoriteSub(subreddit)
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