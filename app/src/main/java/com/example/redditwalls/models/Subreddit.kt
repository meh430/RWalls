package com.example.redditwalls.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteSubreddits")
data class Subreddit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val numSubscribers: Int,
    val icon: String = "",
    val numVisits: Int = 0
)