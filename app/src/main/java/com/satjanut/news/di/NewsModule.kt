package com.satjanut.news.di

import com.satjanut.news.data.response.NewsResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

@Module
@InstallIn(SingletonComponent::class)
object NewsModule {
    @Provides
    fun provideNewsService(
        retrofit: Retrofit
    ): NewsService {
        return retrofit.create(NewsService::class.java)
    }
}

interface NewsService {
    @GET("top-headlines")
    suspend fun topHeadLines(
        @Query("page") page: Int,
        @Query("country") country: String? = "us",
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
    ): NewsResponse

    @GET("everything")
    suspend fun everything(
        @Query("page") page: Int,
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
    ): NewsResponse
}
