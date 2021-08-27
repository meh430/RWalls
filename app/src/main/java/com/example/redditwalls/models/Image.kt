package com.example.redditwalls.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Favorites")
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val imageLink: String = "",
    val postLink: String = "",
    val subreddit: String = "",
    val previewLink: String = ""
) : Parcelable
