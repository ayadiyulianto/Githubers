package com.akusuka.githubers.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akusuka.githubers.database.FavoriteDao
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = FavoriteDao.TABLE_NAME)
@Parcelize
data class Users (
    @PrimaryKey
    @NonNull
    @SerializedName("login")
    var username: String = "",
    var avatar_url: String = ""
) : Parcelable

data class User (
    @SerializedName("login")
    val username: String = "",
    val avatar_url: String = "",
    val name: String = "",
    val company: String = "",
    val blog: String = "",
    val location: String = "",
    val email: String = "",
    val bio: String = "",
    val public_repos: Int = 0,
    val followers: Int = 0,
    val following: Int = 0
)