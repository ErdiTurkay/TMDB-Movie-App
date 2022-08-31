package com.example.challenge4.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ActorURL(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("cast")
    @Expose
    val cast: List<Actor>
)

@Entity
@Parcelize
data class Actor(
    @ColumnInfo
    @SerializedName("adult")
    @Expose
    var adult: Boolean,

    @ColumnInfo
    @SerializedName("gender")
    @Expose
    var gender: Int,

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int,

    @ColumnInfo
    @SerializedName("known_for_department")
    @Expose
    var knownForDepartment: String,

    @ColumnInfo
    @SerializedName("name")
    @Expose
    var name: String,

    @ColumnInfo
    @SerializedName("original_name")
    @Expose
    var originalName: String,

    @ColumnInfo
    @SerializedName("popularity")
    @Expose
    var popularity: Double,

    @ColumnInfo
    @SerializedName("profile_path")
    @Expose
    var profilePath: String?,

    @ColumnInfo
    @SerializedName("cast_id")
    @Expose
    var castId: Int,

    @ColumnInfo
    @SerializedName("character")
    @Expose
    var character: String,

    @ColumnInfo
    @SerializedName("credit_id")
    @Expose
    var creditId: String,

    @ColumnInfo
    @SerializedName("order")
    @Expose
    var order: Int,

    @ColumnInfo
    var isFavorite: Boolean
) : Parcelable
