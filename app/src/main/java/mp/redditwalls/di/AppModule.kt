package mp.redditwalls.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.redditwalls.RWDatabase

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

    @Provides
    fun provideHistoryDAO(db: RWDatabase) = db.getHistoryDAO()

    @Provides
    fun providesPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            "settings",
            Context.MODE_PRIVATE
        )
}