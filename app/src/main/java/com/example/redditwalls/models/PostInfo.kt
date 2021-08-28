package com.example.redditwalls.models

data class PostInfo(
    val subreddit: String,
    val upvotes: String,
    val postTitle: String,
    val author: String,
    val numComments: String,
    val uploadDate: String,
    val imageSize: String,
    val resolution: Resolution = Resolution(0, 0)
) {

    constructor(
        info: PostInfo,
        resolution: Resolution
    ) : this(
        info.subreddit,
        info.upvotes,
        info.postTitle,
        info.author,
        info.numComments,
        info.uploadDate,
        info.imageSize,
        resolution
    )

    companion object {
        fun loading() = PostInfo(
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading",
            Resolution(0, 0)
        )
    }
}