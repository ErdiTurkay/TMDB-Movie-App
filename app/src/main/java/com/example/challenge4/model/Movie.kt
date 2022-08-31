package com.example.challenge4.model

import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MovieURL(
    @SerializedName("page")
    @Expose
    var page: Int,

    @SerializedName("results")
    @Expose
    var results: List<Movie>,

    @SerializedName("total_pages")
    @Expose
    var totalPages: Int,

    @SerializedName("total_results")
    @Expose
    var totalResults: Int
)

@Entity
@Parcelize
data class Movie(
    @ColumnInfo
    @SerializedName("adult")
    @Expose
    var adult: Boolean,

    @ColumnInfo
    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String?,

    @ColumnInfo
    @SerializedName("genre_ids")
    @Expose
    var genreIds: List<Int>,

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int,

    @ColumnInfo
    @SerializedName("original_language")
    @Expose
    var originalLanguage: String,

    @ColumnInfo
    @SerializedName("original_title")
    @Expose
    var originalTitle: String,

    @ColumnInfo
    @SerializedName("overview")
    @Expose
    var overview: String,

    @ColumnInfo
    @SerializedName("popularity")
    @Expose
    var popularity: Double,

    @ColumnInfo
    @SerializedName("poster_path")
    @Expose
    var posterPath: String?,

    @ColumnInfo
    @SerializedName("release_date")
    @Expose
    var releaseDate: String,

    @ColumnInfo
    @SerializedName("title")
    @Expose
    var title: String,

    @ColumnInfo
    @SerializedName("video")
    @Expose
    var video: Boolean,

    @ColumnInfo
    @SerializedName("vote_average")
    @Expose
    var voteAverage: Double,

    @ColumnInfo
    @SerializedName("vote_count")
    @Expose
    var voteCount: Int,

    @ColumnInfo
    var isFavorite: Boolean
) : Parcelable

data class MovieDetail(
    @SerializedName("budget")
    @Expose
    var budget: Int?,

    @SerializedName("revenue")
    @Expose
    var revenue: Int?,

    @SerializedName("runtime")
    @Expose
    var runtime: Int?
)

class Converters {
    @TypeConverter
    fun listToJson(value: List<Int>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Int>::class.java).toList()
}
