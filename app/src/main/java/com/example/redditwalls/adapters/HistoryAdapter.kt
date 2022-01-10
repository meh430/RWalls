package com.example.redditwalls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.databinding.HistoryItemBinding
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.models.History

class HistoryAdapter(private val loadLowRes: Boolean, private val onClick: (History) -> Unit) :
    ListAdapter<History, HistoryAdapter.HistoryViewHolder>(HistoryComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: HistoryItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {

            binding.root.setOnClickListener { onClick(history) }

            val requestOptions = Utils.getGlideRequestOptions()

            Glide.with(binding.historyImage.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(if (loadLowRes) history.previewLink else history.imageLink)
                .placeholder(Utils.getImageLoadingDrawable(binding.historyImage.context))
                .into(binding.historyImage)

            binding.subName.text = if (history.subreddit.startsWith("r/")) {
                history.subreddit
            } else {
                "r/${history.subreddit}"
            }

            val info =
                StringBuilder("Set on ${WallpaperLocation.fromId(history.location).displayText}")
            if (!history.manuallySet) {
                info.append(" through refresh")
            }

            binding.setInfo.text = info
            binding.setDate.text = Utils.convertUTC(history.dateCreated)
        }
    }

    object HistoryComparator : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.imageLink == newItem.imageLink
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }
}