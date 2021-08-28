package com.example.redditwalls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.redditwalls.databinding.ImageItemBinding
import com.example.redditwalls.models.Image

class FavoritesAdapter(
    private val loadLowRes: Boolean,
    private val imageListener: ImageClickListener
) : ListAdapter<Image, ImageViewHolder>(ImageComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            loadLowRes,
            imageListener
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        currentList[position]?.let {
            holder.bind(it)
        }
    }

}