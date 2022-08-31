package com.example.challenge4.feature.detail.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challenge4.MovieAPIService
import com.example.challenge4.database.MovieDatabase
import com.example.challenge4.model.Actor
import com.example.challenge4.model.Movie
import com.example.challenge4.model.MovieDetail
import com.example.challenge4.model.Review
import com.example.challenge4.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val api: MovieAPIService,
    private val db: MovieDatabase.AppDatabase
) : ViewModel() {

    val genreLiveData = MutableLiveData<List<String>>()
    val actorLiveData = MutableLiveData<List<Actor>>()
    val reviewLiveData = MutableLiveData<List<Review>>()
    val videoLiveData = MutableLiveData<List<Video>>()
    val similarMovieLiveData = MutableLiveData<List<Movie>>()
    val movieDetailLiveData = MutableLiveData<MovieDetail>()
    var isOnline = MutableLiveData<Boolean>()
    var isFavorite = MutableLiveData<Boolean>()

    fun initialize(movie: Movie) {
        checkFavorite(movie)
        getGenres(movie.genreIds)
        getActors(movie.id)
        getReviews(movie.id)
        getSimilarMovies(movie.id)
        getVideos(movie.id)
        getDetail(movie.id)
    }

    fun checkFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.Main) {
            viewModelScope.launch(Dispatchers.IO) {
                // Favorite movies are pulled from the Room database.
                val favoriteIDs = db.movieDao().getAllIDs()

                // Checked whether movie is added to favorites.
                movie.isFavorite = favoriteIDs.contains(movie.id)

                // LiveData has been updated.
                isFavorite.postValue(movie.isFavorite)
            }
        }
    }

    private fun getGenres(genreIDs: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Genre list is pulled from the API.
                val genreList = api.getGenresForMovies().genres
                val names: ArrayList<String> = arrayListOf()

                for (item in genreList)
                    if (genreIDs.contains(item.id)) {
                        names.add(item.name)
                    }

                genreLiveData.postValue(names)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getDetail(movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                movieDetailLiveData.postValue(api.getMovieDetail(movieID))

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getActors(movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val castList = api.getActors(movieID).cast
                val favoriteActors = db.actorDao().getAllIDs()
                val actorList: ArrayList<Actor> = arrayListOf()

                for (item in castList) {
                    if (item.knownForDepartment == "Acting") {
                        if (favoriteActors.contains(item.id)) {
                            item.isFavorite = true
                        }
                        actorList.add(item)
                    }
                }

                actorLiveData.postValue(actorList)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getReviews(movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reviewLiveData.postValue(api.getReviews(movieID).results)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getVideos(movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getVideos(movieID).results
                val videoList: ArrayList<Video> = arrayListOf()

                for (item in response) {
                    if (item.site == "YouTube") {
                        videoList.add(item)
                    }
                }

                videoLiveData.postValue(videoList)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getSimilarMovies(movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Similar movies are pulled from the API.
                similarMovieLiveData.postValue(api.getSimilarMovies(movieID).results)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    // When a movie is added to favorites, the isFavorite field is set to true and inserted into the Room database.
    fun changeFavorite(item: Any) {
        if (item is Movie) {
            /* When a movie is added to favorites,
            the isFavorite field is set to true and inserted into the Room database.*/
            if (!item.isFavorite) {
                item.isFavorite = true

                viewModelScope.launch(Dispatchers.IO) {
                    db.movieDao().insert(item)
                }
            }
            /* When a movie is removed from favorites,
            the isFavorite field is set to false and removed from the Room database. */
            else {
                item.isFavorite = false

                viewModelScope.launch(Dispatchers.IO) {
                    db.movieDao().delete(item)
                }
            }
        } else if (item is Actor) {
            /* When a actor is added to favorites,
            the isFavorite field is set to true and inserted into the Room database.*/
            if (!item.isFavorite) {
                item.isFavorite = true

                viewModelScope.launch(Dispatchers.IO) {
                    db.actorDao().insert(item)
                }
            }
            /* When a actor is removed from favorites,
            the isFavorite field is set to false and removed from the Room database. */
            else {
                item.isFavorite = false

                viewModelScope.launch(Dispatchers.IO) {
                    db.actorDao().delete(item)
                }
            }
        }
    }
}
