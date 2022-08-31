package com.example.challenge4.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenreURL(
    @SerializedName("genres")
    @Expose
    var genres: List<Genre>
)

data class Genre(
    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("name")
    @Expose
    var name: String
)
