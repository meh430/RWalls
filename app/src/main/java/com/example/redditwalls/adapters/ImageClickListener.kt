package com.example.redditwalls.adapters

import com.example.redditwalls.models.Image

interface ImageClickListener {
    fun onClick(image: Image)
    fun onLongClick(image: Image)
    fun onDoubleClick(image: Image)
}