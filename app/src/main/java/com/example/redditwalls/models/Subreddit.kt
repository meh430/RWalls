package com.example.redditwalls.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "FavoriteSubreddits")
data class Subreddit @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val numSubscribers: String = "",
    val icon: String = "",
    val isSaved: Boolean = true
) : Parcelable