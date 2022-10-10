package mp.redditwalls.adapters

import android.annotation.SuppressLint
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mp.redditwalls.databinding.ImageItemBinding
import mp.redditwalls.misc.Utils
import mp.redditwalls.misc.toPx
import mp.redditwalls.models.Image
import mp.redditwalls.repositories.ColumnCount


class ImageViewHolder(
    private val binding: ImageItemBinding,
    private val loadLowRes: Boolean,
    private val columnCount: ColumnCount,
    private val imageListener: ImageClickListener
) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(image: Image) {

        // causes noticeable delay due to double tap
        /*val gDetector = GestureDetector(binding.root.context, object : SimpleOnGestureListener() {
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
        })*/
        //binding.imagePreview.setOnTouchListener { _, event -> gDetector.onTouchEvent(event) }

        binding.imagePreview.setOnClickListener {
            imageListener.onClick(it, image)
        }

        binding.imagePreview.setOnLongClickListener {
            imageListener.onLongClick(image)
            true
        }

        binding.imagePreview.apply {
            requestLayout()

            layoutParams.height = if (columnCount.imageHeightDp == -1) {
                RelativeLayout.LayoutParams.MATCH_PARENT
            } else {
                columnCount.imageHeightDp.toPx.toInt()
            }
        }

        val requestOptions = Utils.getGlideRequestOptions()

        Glide.with(binding.imagePreview.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(if (loadLowRes) image.previewLink else image.imageLink)
            .placeholder(Utils.getImageLoadingDrawable(binding.imagePreview.context))
            .into(binding.imagePreview)
    }
}