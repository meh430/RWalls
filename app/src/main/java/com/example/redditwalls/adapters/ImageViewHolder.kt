package com.example.redditwalls.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.redditwalls.databinding.ImageItemBinding
import com.example.redditwalls.models.Image

class ImageViewHolder(
    private val binding: ImageItemBinding,
    private val loadLowRes: Boolean,
    private val imageListener: ImageClickListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: Image) {

        binding.imagePreview.setOnClickListener {
            imageListener.onClick(image)
        }

        binding.imagePreview.setOnLongClickListener {
            imageListener.onLongClick(image)
            true
        }

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .dontAnimate()
            .dontTransform()

        Glide.with(binding.imagePreview.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(if (loadLowRes) image.previewLink else image.imageLink)
            .into(binding.imagePreview)
    }
}