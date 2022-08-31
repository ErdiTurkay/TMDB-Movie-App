package com.example.challenge4.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImageSlider(
    @SerializedName("image_url")
    @Expose
    var imageURL: String,

    @SerializedName("title")
    @Expose
    var title: String
)
