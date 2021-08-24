package com.example.redditwalls.models

data class PostInfo(
    val subreddit: String,
    val upvotes: Int,
    val postTitle: String,
    val author: String,
    val numComments: String,
    val uploadDate: String,
    val imageSize: Int
)