package com.example.redditwalls.repositories

import com.example.redditwalls.datasources.SubredditsDAO
import com.example.redditwalls.models.Subreddit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteSubredditsRepository @Inject constructor(private val subredditsDAO: SubredditsDAO) {
    fun getFavoriteSubredditsFlow() = subredditsDAO.getFavoriteSubredditsFlow()

    suspend fun getFavoriteSubreddits() = withContext(Dispatchers.IO) {
        subredditsDAO.getFavoriteSubreddits()
    }

    suspend fun deleteAllFavorites() = withContext(Dispatchers.IO) {
        subredditsDAO.deleteAllFavorites()
    }

    suspend fun deleteFavoriteSubreddit(subreddit: Subreddit) = withContext(Dispatchers.IO) {
        subredditsDAO.deleteFavoriteSubreddit(subreddit)
    }
}