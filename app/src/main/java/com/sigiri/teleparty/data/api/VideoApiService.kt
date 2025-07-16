package com.sigiri.teleparty.data.api

import com.sigiri.teleparty.data.model.VideoMetadataResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 * API service interface for fetching video metadata
 */
interface VideoApiService {
    @GET("videos/{videoId}/metadata")
    suspend fun getVideoMetadata(@Path("videoId") videoId: String): Response<VideoMetadataResponse>
    
    companion object {
        private const val BASE_URL = "https://api.example.com/" // Replace with actual API base URL
        
        fun create(): VideoApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
                
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VideoApiService::class.java)
        }
    }
}
