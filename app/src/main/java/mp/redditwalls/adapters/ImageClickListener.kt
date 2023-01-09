package mp.redditwalls.adapters

import android.view.View
import mp.redditwalls.models.Image

interface ImageClickListener {
    fun onClick(view: View?, image: Image)
    fun onLongClick(image: Image)
    fun onDoubleClick(image: Image)
}