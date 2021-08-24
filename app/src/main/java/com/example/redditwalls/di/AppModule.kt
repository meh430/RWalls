package com.example.redditwalls.di

import android.content.Context
import com.example.redditwalls.RWDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRWDatabase(@ApplicationContext context: Context) =
        RWDatabase.getDatabase(context)

    @Provides
    fun provideFavoriteImagesDAO(db: RWDatabase) = db.getFavoritesDAO()

    @Provides
    fun provideFavoriteSubredditsDAO(db: RWDatabase) = db.getSubredditDAO()
}