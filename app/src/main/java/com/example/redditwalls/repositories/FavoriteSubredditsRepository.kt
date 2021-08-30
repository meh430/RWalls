package com.example.redditwalls.repositories

import com.example.redditwalls.datasources.SubredditsDAO
import com.example.redditwalls.models.Subreddit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteSubredditsRepository @Inject constructor(private val subredditsDAO: SubredditsDAO) {

    suspend fun insertFavoriteSubreddit(subreddit: Subreddit) = withContext(Dispatchers.IO) {
        subredditsDAO.insertFavoriteSubreddit(subreddit)
    }

    fun getFavoriteSubredditsLiveData() = subredditsDAO.getFavoriteSubredditsLiveData()

    suspend fun getFavoriteSubreddits() = withContext(Dispatchers.IO) {
        subredditsDAO.getFavoriteSubreddits()
    }

    suspend fun deleteAllFavorites() = withContext(Dispatchers.IO) {
        subredditsDAO.deleteAllFavorites()
    }

    suspend fun deleteFavoriteSubreddit(name: String) = withContext(Dispatchers.IO) {
        subredditsDAO.deleteFavoriteSubreddit(name)
    }

    suspend fun isSaved(name: String) = subredditsDAO.isSaved(name)

}