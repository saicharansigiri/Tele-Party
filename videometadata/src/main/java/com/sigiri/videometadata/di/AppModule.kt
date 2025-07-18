package com.sigiri.videometadata.di

import com.sigiri.videometadata.data.remote.VideoMetadataApi
import com.sigiri.videometadata.data.repository.VideoRepository
import com.sigiri.videometadata.data.repository.VideoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl() = "https://api.mxplayer.in/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log full request/response body
        }

    @Provides
    @Singleton
    fun provideUserAgentInterceptor(): Interceptor =
        Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Android)")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: Interceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()


    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideVideoApi(retrofit: Retrofit): VideoMetadataApi =
        retrofit.create(VideoMetadataApi::class.java)

    @Provides
    @Singleton
    fun provideVideoRepository(api: VideoMetadataApi): VideoRepository =
        VideoRepositoryImpl(api)
}
