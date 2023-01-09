package mp.redditwalls.adapters

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mp.redditwalls.databinding.ImagePageItemBinding
import mp.redditwalls.utils.Utils
import mp.redditwalls.models.Image

interface ImagePageListener : ImageClickListener {
    fun onSetWallpaper(image: Image)
    fun onClickInfo(image: Image)
    fun onLike(image: Image)
}

class ImagePageViewHolder(
    private val binding: ImagePageItemBinding,
    private val imageListener: ImagePageListener
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(image: Image) = binding.apply {
        // image click listeners
        val gDetector = GestureDetector(
            binding.root.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    imageListener.onLike(image)
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    super.onLongPress(e)
                    imageListener.onLongClick(image)
                }
            })
        binding.imagePreview.setOnTouchListener { _, event -> gDetector.onTouchEvent(event) }


        // icon click listeners
        binding.setWallpaper.setOnClickListener { imageListener.onSetWallpaper(image) }
        binding.info.setOnClickListener { imageListener.onClickInfo(image) }

        val requestOptions = Utils.getGlideRequestOptions()
        Glide.with(imagePreview.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(image.imageLink)
            .placeholder(Utils.getImageLoadingDrawable(binding.imagePreview.context))
            .into(binding.imagePreview)
    }
}