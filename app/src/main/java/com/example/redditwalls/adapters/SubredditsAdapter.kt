package com.example.redditwalls.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.redditwalls.R
import com.example.redditwalls.databinding.SubredditItemBinding
import com.example.redditwalls.models.Subreddit
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SubredditsAdapter(private val listener: SubredditClickListener) :
    ListAdapter<Subreddit, SubredditsAdapter.SubredditViewHolder>(SubredditComparator) {

    enum class SubredditMenuOptions(val displayText: String) {
        FAVORITE("Add to favorites"),
        UNFAVORITE("Remove from favorites"),
        DEFAULT("Set as default"),
        LAUNCH_WEB("Open in browser"),
        BROWSE("Browse images");

        companion object {
            fun fromText(text: String) = values().find { it.displayText == text }
        }
    }

    interface SubredditClickListener {
        fun onClick(subreddit: Subreddit)
        fun onMenuItemClick(subreddit: Subreddit, option: SubredditMenuOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        return SubredditViewHolder(
            SubredditItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SubredditViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class SubredditViewHolder(
        private val binding: SubredditItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private fun showOptionsDialog(context: Context, subreddit: Subreddit) {
            val items = SubredditMenuOptions.values().filter {
                val target = if (subreddit.isSaved) {
                    SubredditMenuOptions.FAVORITE
                } else {
                    SubredditMenuOptions.UNFAVORITE
                }

                it != target
            }.map { it.displayText }.toTypedArray()
            MaterialAlertDialogBuilder(context).setTitle("Options").setItems(items) { _, i ->
                SubredditMenuOptions.fromText(items[i])?.let {
                    listener.onMenuItemClick(subreddit, it)
                }
            }.show()
        }

        fun bind(subreddit: Subreddit) = binding.apply {
            setSubreddit(subreddit)
            subscriberCount.isVisible = subreddit.numSubscribers.isNotEmpty()
            subDescription.isVisible = subreddit.description.isNotBlank()
            root.setOnClickListener {
                listener.onClick(subreddit)
            }
            root.setOnLongClickListener {
                showOptionsDialog(it.context, subreddit)
                true
            }

            options.setOnClickListener {
                showOptionsDialog(it.context, subreddit)
            }

            if (subreddit.icon.isEmpty() || subreddit.icon.isBlank()) {
                val def = ContextCompat.getDrawable(
                    subIcon.context,
                    R.drawable.ic_info
                )
                subIcon.setImageDrawable(def)
            } else {
                Glide.with(subIcon.context)
                    .load(subreddit.icon)
                    .override(200, 200)
                    .centerCrop()
                    .into(subIcon)
            }
        }
    }

    object SubredditComparator : DiffUtil.ItemCallback<Subreddit>() {
        override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
            return oldItem == newItem
        }

    }
}