package com.akusuka.favoriteofgithubers.connection

import com.akusuka.favoriteofgithubers.BuildConfig
import com.akusuka.favoriteofgithubers.model.Repository
import com.akusuka.favoriteofgithubers.model.User
import com.akusuka.favoriteofgithubers.model.Users
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface GitHubService {
    companion object {
        private const val PAT = BuildConfig.API_KEY
        const val TOKEN = "Authorization: token $PAT"
    }

    @Headers(TOKEN)
    @GET("users")
    fun getRandomUsers(): Call<ArrayList<Users>?>?

    @Headers(TOKEN)
    @GET("search/users")
    fun getUsers(
        @Query("q") query: String
    ): Call<CallbackUsers?>?

    @Headers(TOKEN)
    @GET("users/{username}")
    fun getUser(
        @Path("username") username: String?
    ): Call<User?>?

    @Headers(TOKEN)
    @GET("users/{username}/followers")
    fun getFollowers(
        @Path("username") username: String?
    ): Call<ArrayList<Users>?>?

    @Headers(TOKEN)
    @GET("users/{username}/following")
    fun getFollowing(
        @Path("username") username: String?
    ): Call<ArrayList<Users>?>?

    @Headers(TOKEN)
    @GET("users/{username}/repos")
    fun getRepository(
        @Path("username") username: String?
    ): Call<ArrayList<Repository>?>?
}