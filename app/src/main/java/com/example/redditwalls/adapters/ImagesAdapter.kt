package com.example.redditwalls.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.redditwalls.databinding.ImageItemBinding
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.ColumnCount

class ImagesAdapter(
    private val loadLowRes: Boolean,
    private var columnCount: ColumnCount,
    private val imageListener: ImageClickListener
) :
    PagingDataAdapter<Image, ImageViewHolder>(ImageComparator) {

    // Need to notify data set so views are re-rendered with new height
    @SuppressLint("NotifyDataSetChanged")
    fun setCount(count: ColumnCount) {
        columnCount = count
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(image = it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            loadLowRes,
            columnCount,
            imageListener
        )
}

