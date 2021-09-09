package com.example.redditwalls.adapters

import android.view.View
import com.example.redditwalls.models.Image

interface ImageClickListener {
    fun onClick(view: View?, image: Image)
    fun onLongClick(image: Image)
    fun onDoubleClick(image: Image)
}