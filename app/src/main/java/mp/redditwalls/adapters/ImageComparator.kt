package mp.redditwalls.adapters

import androidx.recyclerview.widget.DiffUtil
import mp.redditwalls.models.Image


object ImageComparator : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.imageLink == newItem.imageLink
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}