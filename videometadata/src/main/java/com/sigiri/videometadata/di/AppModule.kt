package com.sigiri.videometadata.di

import com.sigiri.videometadata.data.remote.VideoMetadataApi
import com.sigiri.videometadata.data.repository.VideoRepository
import com.sigiri.videometadata.data.repository.VideoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl() = "https://api.mxplayer.in/"

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
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
