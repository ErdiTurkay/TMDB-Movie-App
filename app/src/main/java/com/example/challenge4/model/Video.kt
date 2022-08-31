package com.example.challenge4.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideoURL(
    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("results")
    @Expose
    var results: List<Video>
)

data class Video(
    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("key")
    @Expose
    var key: String,

    @SerializedName("site")
    @Expose
    var site: String
)
