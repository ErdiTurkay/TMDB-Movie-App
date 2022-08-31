package com.example.challenge4.feature.list.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challenge4.database.MovieDatabase
import com.example.challenge4.model.Actor
import com.example.challenge4.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val db: MovieDatabase.AppDatabase
) : ViewModel() {
    var favoriteMoviesLiveData = MutableLiveData<List<Movie>>()
    var favoriteActorsLiveData = MutableLiveData<List<Actor>>()
    var tabPosition = 0

    fun initialize() {
        getFavoriteMovies()
        getFavoriteActors()
    }

    fun getFavoriteMovies() {
        // Favorite movies are pulled from the Room database.
        viewModelScope.launch(Dispatchers.IO) {
            favoriteMoviesLiveData.postValue(db.movieDao().getAll())
        }
    }

    fun getFavoriteActors() {
        // Favorite actors are pulled from the Room database.
        viewModelScope.launch(Dispatchers.IO) {
            favoriteActorsLiveData.postValue(db.actorDao().getAll())
        }
    }

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
