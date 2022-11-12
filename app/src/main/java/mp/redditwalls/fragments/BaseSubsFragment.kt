package mp.redditwalls.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import mp.redditwalls.adapters.SubredditsAdapter
import mp.redditwalls.adapters.SubredditsAdapter.SubredditClickListener
import mp.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions
import mp.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.BROWSE
import mp.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.DEFAULT
import mp.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.FAVORITE
import mp.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.LAUNCH_WEB
import mp.redditwalls.adapters.SubredditsAdapter.SubredditMenuOptions.UNFAVORITE
import mp.redditwalls.datasources.RWApi.Companion.BASE
import mp.redditwalls.utils.launchBrowser
import mp.redditwalls.utils.removeSubPrefix
import mp.redditwalls.models.Subreddit
import mp.redditwalls.viewmodels.FavoriteSubsViewModel
import mp.redditwalls.viewmodels.SettingsViewModel

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