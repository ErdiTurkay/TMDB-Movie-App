package com.example.challenge4.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Person(
    @SerializedName("adult")
    @Expose
    var adult: Boolean,

    @SerializedName("also_known_as")
    @Expose
    var alsoKnownAs: List<String>,

    @SerializedName("biography")
    @Expose
    var biography: String,

    @SerializedName("birthday")
    @Expose
    var birthday: String?,

    @SerializedName("gender")
    @Expose
    var gender: Int,

    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("imdb_id")
    @Expose
    var imdbId: String,

    @SerializedName("known_for_department")
    @Expose
    var knownForDepartment: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("place_of_birth")
    @Expose
    var placeOfBirth: String?,

    @SerializedName("popularity")
    @Expose
    var popularity: Double,

    @SerializedName("profile_path")
    @Expose
    var profilePath: String
)
