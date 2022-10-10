package mp.redditwalls.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named
import mp.redditwalls.network.AccessToken
import mp.redditwalls.network.Constants
import mp.redditwalls.network.TokenAuthenticator
import mp.redditwalls.network.deserializers.NetworkImageDeserializer
import mp.redditwalls.network.deserializers.NetworkImagesDeserializer
import mp.redditwalls.network.deserializers.NetworkSubredditDeserializer
import mp.redditwalls.network.deserializers.NetworkSubredditsDeserializer
import mp.redditwalls.network.models.NetworkImage
import mp.redditwalls.network.models.NetworkImages
import mp.redditwalls.network.models.NetworkSubreddit
import mp.redditwalls.network.models.NetworkSubreddits
import mp.redditwalls.network.services.AuthService
import mp.redditwalls.network.services.ImagesService
import mp.redditwalls.network.services.SubredditsService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    fun provideAuthService(
        @Named("AuthRetrofit") retrofit: Retrofit
    ) = retrofit.create(AuthService::class.java)

    @Provides
    fun provideImagesService(
        @Named("MainRetrofit") retrofit: Retrofit
    ) = retrofit.create(ImagesService::class.java)

    @Provides
    fun provideSubredditsService(
        @Named("MainRetrofit") retrofit: Retrofit
    ) = retrofit.create(SubredditsService::class.java)

    @Named("MainRetrofit")
    @Provides
    fun provideMainRetrofit(
        @Named("MainOkhttpClient")
        client: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder()
        .baseUrl(Constants.BASE_OAUTH_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @Named("AuthRetrofit")
    @Provides
    fun provideAuthRetrofit(
        @Named("AuthOkhttpClient")
        client: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @Named("AuthOkhttpClient")
    @Provides
    fun provideAuthOkhttpClient() = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .header("Authorization", Constants.CREDENTIALS)
                .build()
            it.proceed(request)
        }.build()

    @Named("MainOkhttpClient")
    @Provides
    fun provideMainOkhttpClient(authenticator: TokenAuthenticator) = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .header("Authorization", "Bearer ${AccessToken.accessToken}")
                .build()
            it.proceed(request)
        }.authenticator(authenticator).build()

    @Provides
    fun provideGson(
        networkImageDeserializer: NetworkImageDeserializer,
        networkImagesDeserializer: NetworkImagesDeserializer,
        networkSubredditDeserializer: NetworkSubredditDeserializer,
        networkSubredditsDeserializer: NetworkSubredditsDeserializer
    ) = GsonBuilder()
        .registerTypeAdapter(NetworkImage::class.java, networkImageDeserializer)
        .registerTypeAdapter(NetworkImages::class.java, networkImagesDeserializer)
        .registerTypeAdapter(NetworkSubreddit::class.java, networkSubredditDeserializer)
        .registerTypeAdapter(NetworkSubreddits::class.java, networkSubredditsDeserializer)
        .create()
}