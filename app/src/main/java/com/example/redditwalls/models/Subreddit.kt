package com.example.redditwalls.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteSubreddits")
data class Subreddit @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val numSubscribers: String = "",
    val icon: String = "",
    val isSaved: Boolean = true
)