package com.example.challenge4

import android.app.Application
import com.example.challenge4.model.ActorURL
import com.example.challenge4.model.GenreURL
import com.example.challenge4.model.ImageSlider
import com.example.challenge4.model.MovieDetail
import com.example.challenge4.model.MovieURL
import com.example.challenge4.model.Person
import com.example.challenge4.model.ReviewURL
import com.example.challenge4.model.StarringMovieURL
import com.example.challenge4.model.VideoURL
import dagger.hilt.android.HiltAndroidApp
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@HiltAndroidApp
class Challenge4Application : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

interface MovieAPIService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): MovieURL

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieID: Int
    ): MovieDetail

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int
    ): MovieURL

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int
    ): MovieURL

    @GET("genre/movie/list")
    suspend fun getGenresForMovies(): GenreURL

    @GET("movie/{movie_id}/credits")
    suspend fun getActors(
        @Path("movie_id") movieID: Int
    ): ActorURL

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(
        @Path("movie_id") movieID: Int
    ): ReviewURL

    @GET("movie/{movie_id}/videos")
    suspend fun getVideos(
        @Path("movie_id") movieID: Int
    ): VideoURL

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieID: Int
    ): MovieURL

    @GET("person/{person_id}")
    suspend fun getPerson(
        @Path("person_id") personID: Int
    ): Person

    @GET("search/movie")
    suspend fun getSearch(
        @Query("query") query: String,
        @Query("page") page: Int
    ): MovieURL

    @GET("person/{person_id}/movie_credits")
    suspend fun getStarringMovies(
        @Path("person_id") personID: Int
    ): StarringMovieURL
}