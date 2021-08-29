package com.example.redditwalls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.redditwalls.databinding.ImageItemBinding
import com.example.redditwalls.models.Image

class ImagesAdapter(
    private val loadLowRes: Boolean,
    private val imageListener: ImageClickListener
) :
    PagingDataAdapter<Image, ImageViewHolder>(ImageComparator) {

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(image = it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            loadLowRes,
            imageListener
        )
}

