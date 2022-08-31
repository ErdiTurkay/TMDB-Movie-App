package com.example.challenge4.feature.list.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challenge4.MovieAPIService
import com.example.challenge4.database.MovieDatabase
import com.example.challenge4.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private var api: MovieAPIService,
    private var db: MovieDatabase.AppDatabase
) : ViewModel() {

    var searchMoviesLiveData = MutableLiveData<List<Movie>?>()
    var movieList = arrayListOf<Movie>()
    var page = 1
    var totalPage = 900
    var isOnline = MutableLiveData<Boolean>()

    fun getTopRatedMovies(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Result is pulled from the API.
                val response = api.getSearch(page = page, query = query)
                val searchList = response.results

                // Favorite movies are pulled from the Room database.
                val favoriteIDs = db.movieDao().getAllIDs()

                // Movies are checked if they are among the favorite movies.
                for (item in searchList) {
                    if (favoriteIDs.contains(item.id)) {
                        item.isFavorite = true
                    }
                }

                // The totalPage variable has been updated according to the totalPage field from the API.
                totalPage = response.totalPages

                // The movie list has been updated and posted to LiveData.
                movieList.addAll(searchList)
                searchMoviesLiveData.postValue(movieList)

                // The page variable is incremented by 1 for the other query.
                page++

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
            val searchMovieList = searchMoviesLiveData.value

            // Checked whether each movie in the list is added to favorites.
            if (searchMovieList?.isNotEmpty() == true) {
                for (item in searchMovieList) {
                    item.isFavorite = favoriteIDs.contains(item.id)
                }
            }

            // LiveData has been updated.
            searchMoviesLiveData.postValue(searchMovieList)
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
