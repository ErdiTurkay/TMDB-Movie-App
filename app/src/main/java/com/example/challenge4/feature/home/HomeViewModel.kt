package com.example.challenge4.feature.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denzcoskun.imageslider.models.SlideModel
import com.example.challenge4.ImageSliderAPIService
import com.example.challenge4.MovieAPIService
import com.example.challenge4.database.MovieDatabase
import com.example.challenge4.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var imageSliderApi: ImageSliderAPIService,
    var api: MovieAPIService,
    var db: MovieDatabase.AppDatabase
) : ViewModel() {

    val imageSliderLiveData = MutableLiveData<List<SlideModel>>()
    val popularMoviesLiveData = MutableLiveData<List<Movie>?>()
    val topRatedMoviesLiveData = MutableLiveData<List<Movie>?>()
    val upcomingMoviesLiveData = MutableLiveData<List<Movie>?>()
    var isOnline = MutableLiveData<Boolean>()

    init {
        getPopularMovies()
        getUpcomingMovies()
        getTopRatedMovies()
        getImageSlider()
    }

    fun initialize() {
        getPopularMovies()
        getUpcomingMovies()
        getTopRatedMovies()
        getImageSlider()
    }

    private fun getImageSlider() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Image sliders are pulled from the API.
                val response = imageSliderApi.getImageSlider()

                // SlideModel list is created.
                val slideModelList: ArrayList<SlideModel> = arrayListOf()

                /* For each ImageSlider object from the API,
                the SlideModel has been created and added to the list. */
                for (item in response) {
                    slideModelList.add(SlideModel(item.imageURL, item.title))
                }

                // SlideModel list posted to LiveData.
                imageSliderLiveData.postValue(slideModelList)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Popular movies are pulled from the API.
                val response = api.getPopularMovies(page = 1).results

                // Favorite movies are pulled from the Room database.
                val favoriteIDs = db.movieDao().getAllIDs()

                // Movies are checked if they are among the favorite movies.
                for (item in response) {
                    if (favoriteIDs.contains(item.id)) {
                        item.isFavorite = true
                    }
                }

                // The movies are posted to LiveData.
                popularMoviesLiveData.postValue(response)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getTopRatedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Top rated movies are pulled from the API.
                val response = api.getTopRatedMovies(page = 1).results

                // Favorite movies are pulled from the Room database.
                val favoriteIDs = db.movieDao().getAllIDs()

                // Movies are checked if they are among the favorite movies.
                for (item in response) {
                    if (favoriteIDs.contains(item.id)) {
                        item.isFavorite = true
                    }
                }

                // The movies are posted to LiveData.
                topRatedMoviesLiveData.postValue(response)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getUpcomingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Top rated movies are pulled from the API.
                val response = api.getUpcomingMovies(page = 1).results

                // Favorite movies are pulled from the Room database.
                val favoriteIDs = db.movieDao().getAllIDs()

                // Movies are checked if they are among the favorite movies.
                for (item in response) {
                    if (favoriteIDs.contains(item.id)) {
                        item.isFavorite = true
                    }
                }

                // The movies are posted to LiveData.
                upcomingMoviesLiveData.postValue(response)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    fun checkFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            // Favorite movies are pulled from the Room database.
            val favoriteIDs = db.movieDao().getAllIDs()
            val popularMovieList = popularMoviesLiveData.value
            val topRatedMovieList = topRatedMoviesLiveData.value
            val upcomingMovieList = upcomingMoviesLiveData.value

            // Checked whether each movie in the list is added to favorites.
            if (popularMovieList?.isNotEmpty() == true) {
                for (item in popularMovieList) {
                    item.isFavorite = favoriteIDs.contains(item.id)
                }
            }

            // Checked whether each movie in the list is added to favorites.
            if (topRatedMovieList?.isNotEmpty() == true) {
                for (item in topRatedMovieList) {
                    item.isFavorite = favoriteIDs.contains(item.id)
                }
            }

            // Checked whether each movie in the list is added to favorites.
            if (upcomingMovieList?.isNotEmpty() == true) {
                for (item in upcomingMovieList) {
                    item.isFavorite = favoriteIDs.contains(item.id)
                }
            }

            // LiveData have been updated.
            popularMoviesLiveData.postValue(popularMovieList)
            topRatedMoviesLiveData.postValue(topRatedMovieList)
            upcomingMoviesLiveData.postValue(upcomingMovieList)
        }
    }

    fun changeFavorite(movie: Movie) {
        /* When a movie is added to favorites,
        the isFavorite field is set to true and inserted into the Room database. */
        if (!movie.isFavorite) {
            movie.isFavorite = true
            viewModelScope.launch(Dispatchers.IO) {
                db.movieDao().insert(movie)
            }
        }
        /* When a movie is removed from favorites,
        the isFavorite field is set to false and removed from the Room database. */
        else {
            movie.isFavorite = false
            viewModelScope.launch(Dispatchers.IO) {
                db.movieDao().delete(movie)
            }
        }
    }
}
