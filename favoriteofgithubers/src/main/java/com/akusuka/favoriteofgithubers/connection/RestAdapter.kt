package com.akusuka.favoriteofgithubers.connection

import com.akusuka.favoriteofgithubers.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RestAdapter {

    companion object {
        const val BASE_URL = "https://api.github.com/"
        val gitHubService: GitHubService = RestAdapter()
            .createAPI()
    }

    fun createAPI(): GitHubService {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging)
        }
        builder.cache(null)
        val okHttpClient = builder.build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create<GitHubService>(
            GitHubService::class.java)
    }
}