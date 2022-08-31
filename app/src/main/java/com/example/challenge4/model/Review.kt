package com.example.challenge4.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewURL(
    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("page")
    @Expose
    var page: Int,

    @SerializedName("results")
    @Expose
    var results: List<Review>,

    @SerializedName("total_pages")
    @Expose
    var totalPages: Int,

    @SerializedName("total_results")
    @Expose
    var totalResults: Int
)

data class Review(
    @SerializedName("author")
    @Expose
    val author: String,

    @SerializedName("author_details")
    @Expose
    val authorDetails: AuthorDetails,

    @SerializedName("content")
    @Expose
    val content: String,

    @SerializedName("created_at")
    @Expose
    val createdAt: String,

    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String,

    @SerializedName("url")
    @Expose
    val url: String
)

data class AuthorDetails(

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("username")
    @Expose
    val username: String,

    @SerializedName("avatar_path")
    @Expose
    val avatarPath: String
)
