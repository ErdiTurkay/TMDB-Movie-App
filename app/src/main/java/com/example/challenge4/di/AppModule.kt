package com.example.challenge4.di

import android.content.Context
import androidx.room.Room
import com.example.challenge4.ImageSliderAPIService
import com.example.challenge4.MovieAPIService
import com.example.challenge4.common.Constant
import com.example.challenge4.database.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideAPI(): MovieAPIService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val url = chain
                    .request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", Constant.API_KEY)
                    .addQueryParameter("language", Constant.API_LANGUAGE)
                    .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(MovieAPIService::class.java)
    }

    @Provides
    fun provideImageSliderAPI(): ImageSliderAPIService {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.IMAGE_SLIDER_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ImageSliderAPIService::class.java)
    }

    @Provides
    fun provideRoom(@ApplicationContext context: Context): MovieDatabase.AppDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase.AppDatabase::class.java,
            "movie-database"
        ).build()
    }
}
