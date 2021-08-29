package com.example.redditwalls.adapters

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
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
    @SuppressLint("ClickableViewAccessibility")
    fun bind(image: Image) {

        val gDetector = GestureDetector(binding.root.context, object : SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                imageListener.onClick(image)
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                imageListener.onDoubleClick(image)
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                imageListener.onLongClick(image)
            }
        })

        binding.imagePreview.setOnTouchListener { _, event -> gDetector.onTouchEvent(event) }

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