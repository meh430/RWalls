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
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    fun provideRedditAuthService(
        @Named("RedditAuthRetrofit") retrofit: Retrofit
    ) = retrofit.create(AuthService::class.java)

    @Provides
    fun provideImagesService(
        @Named("RedditRetrofit") retrofit: Retrofit
    ) = retrofit.create(ImagesService::class.java)

    @Provides
    fun provideSubredditsService(
        @Named("RedditRetrofit") retrofit: Retrofit
    ) = retrofit.create(SubredditsService::class.java)

    @Named("RedditRetrofit")
    @Provides
    fun provideRedditRetrofit(
        @Named("RedditOkhttpClient")
        client: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder()
        .baseUrl(Constants.BASE_REDDIT_OAUTH_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @Named("RedditAuthRetrofit")
    @Provides
    fun provideRedditAuthRetrofit(
        @Named("RedditAuthOkhttpClient")
        client: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder()
        .baseUrl(Constants.BASE_REDDIT_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @Named("ImgurRetrofit")
    @Provides
    fun provideImgurRetrofit(
        @Named("ImgurOkhttpClient")
        client: OkHttpClient,
    ) = Retrofit.Builder()
        .baseUrl(Constants.BASE_IMGUR_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()

    @Named("RedditAuthOkhttpClient")
    @Provides
    fun provideRedditAuthOkhttpClient() = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .header("Authorization", Constants.CREDENTIALS)
                .header("Content-Type", "application/json")
                .build()
            it.proceed(request)
        }.build()

    @Named("RedditOkhttpClient")
    @Provides
    fun provideRedditOkhttpClient(authenticator: TokenAuthenticator) = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${AccessToken.accessToken}")
                .build()
            it.proceed(request)
        }.addNetworkInterceptor {
            val response = it.proceed(it.request())
            if (response.code == 301 || response.code == 403) {
                response.newBuilder().code(401).build()
            } else {
                response
            }
        }.authenticator(authenticator).build()

    @Named("ImgurOkhttpClient")
    @Provides
    fun provideImgurOkhttpClient() = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .header("Authorization", "Bearer ${Constants.IMGUR_CLIENT_ID}")
                .header("Content-Type", "application/json")
                .build()
            it.proceed(request)
        }.build()

    @Provides
    fun provideGson(
        networkImageDeserializer: NetworkImageDeserializer,
        networkImagesDeserializer: NetworkImagesDeserializer,
        networkSubredditDeserializer: NetworkSubredditDeserializer,
        networkSubredditsDeserializer: NetworkSubredditsDeserializer
    ) = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(NetworkImage::class.java, networkImageDeserializer)
        .registerTypeAdapter(NetworkImages::class.java, networkImagesDeserializer)
        .registerTypeAdapter(NetworkSubreddit::class.java, networkSubredditDeserializer)
        .registerTypeAdapter(NetworkSubreddits::class.java, networkSubredditsDeserializer)
        .create()
}
