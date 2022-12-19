package mp.redditwalls.local.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.redditwalls.local.databases.LocalDatabase

@Module
@InstallIn(SingletonComponent::class)
internal object LocalModule {
    @Provides
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ) = LocalDatabase.getDatabase(context)

    @Provides
    fun provideDbImageDao(localDatabase: LocalDatabase) = localDatabase.getDbImageDao()

    @Provides
    fun provideDbImageFolderDao(localDatabase: LocalDatabase) = localDatabase.getDbImageFolderDao()

    @Provides
    fun provideDbRecentActivityItemDao(localDatabase: LocalDatabase) =
        localDatabase.getDbRecentActivityItemDao()

    @Provides
    fun provideDbSubredditDao(localDatabase: LocalDatabase) = localDatabase.getDbSubredditDao()
}