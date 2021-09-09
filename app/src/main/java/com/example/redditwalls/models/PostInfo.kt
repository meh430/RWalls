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
        fun loading() = message("Loading...")

        fun error(errorMessage: String) = message("Error").copy(postTitle = errorMessage)

        private fun message(msg: String) = PostInfo(
            msg,
            msg,
            msg,
            msg,
            msg,
            msg,
            msg,
            Resolution(0, 0)
        )
    }
}