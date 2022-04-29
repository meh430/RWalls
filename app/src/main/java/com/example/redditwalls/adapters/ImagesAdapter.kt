package com.example.redditwalls.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.redditwalls.databinding.ImageItemBinding
import com.example.redditwalls.databinding.ImagePageItemBinding
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.ColumnCount

class ImagesAdapter(
    private val loadLowRes: Boolean,
    private var columnCount: ColumnCount,
    private val imagePageListener: ImagePageListener,
    private var isCarousel: Boolean = false
) :
    PagingDataAdapter<Image, RecyclerView.ViewHolder>(ImageComparator) {

    // Need to notify data set so views are re-rendered with new height
    @SuppressLint("NotifyDataSetChanged")
    fun setCount(count: ColumnCount) {
        columnCount = count
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setIsCarousel(isCarousel: Boolean) {
        this.isCarousel = isCarousel
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.also {
            if (holder is ImagePageViewHolder) {
                holder.bind(it)
            } else if (holder is ImageViewHolder) {
                holder.bind(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (isCarousel) {
        ImagePageViewHolder(
            ImagePageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), imageListener = imagePageListener
        )
    } else {
        ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            loadLowRes,
            columnCount,
            imagePageListener
        )
    }
}

