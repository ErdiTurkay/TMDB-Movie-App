package com.example.challenge4.feature.detail.actor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challenge4.MovieAPIService
import com.example.challenge4.database.MovieDatabase
import com.example.challenge4.model.Actor
import com.example.challenge4.model.Cast
import com.example.challenge4.model.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ActorDetailViewModel @Inject constructor(
    private val api: MovieAPIService,
    private val db: MovieDatabase.AppDatabase
) : ViewModel() {

    var personLiveData = MutableLiveData<Person>()
    var castsLiveData = MutableLiveData<List<Cast>>()
    var isOnline = MutableLiveData<Boolean>()
    var isFavorite = MutableLiveData<Boolean>()

    fun initialize(actor: Actor) {
        checkFavorite(actor)
        getPerson(actor)
        getStarringMovies(actor)
    }

    fun checkFavorite(actor: Actor) {
        viewModelScope.launch(Dispatchers.Main) {
            viewModelScope.launch(Dispatchers.IO) {
                // Favorite actors are pulled from the Room database.
                val favoriteIDs = db.actorDao().getAllIDs()

                // Checked whether actor is added to favorites.
                actor.isFavorite = favoriteIDs.contains(actor.id)

                // LiveData has been updated.
                isFavorite.postValue(actor.isFavorite)
            }
        }
    }

    private fun getPerson(actor: Actor) {
        // Actor's person information is pulled from the API.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Person details is posted to LiveData.
                personLiveData.postValue(api.getPerson(actor.id))

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    private fun getStarringMovies(actor: Actor) {
        // Actor's starring movies are pulled from the API.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Starring movies are posted to LiveData.
                castsLiveData.postValue(api.getStarringMovies(actor.id).cast)

                // If no error occurred in the try code block, the isOnline variable is set to true.
                isOnline.postValue(true)
            } catch (e: IOException) {
                // isOnline LiveData is set to false when internet error occurs.
                isOnline.postValue(false)
            }
        }
    }

    fun changeFavorite(actor: Actor) {
        /* When a actor is added to favorites,
        the isFavorite field is set to true and inserted into the Room database. */
        if (!actor.isFavorite) {
            actor.isFavorite = true
            viewModelScope.launch(Dispatchers.IO) {
                db.actorDao().insert(actor)
            }
        }
        /* When a actor is removed from favorites,
        the isFavorite field is set to false and removed from the Room database. */
        else {
            actor.isFavorite = false
            viewModelScope.launch(Dispatchers.IO) {
                db.actorDao().delete(actor)
            }
        }
    }
}
