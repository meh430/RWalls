package com.example.redditwalls.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Favorites")
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val imageLink: String = "",
    val postLink: String = "",
    val subreddit: String = "",
    val previewLink: String = ""
)
