package com.akusuka.favoriteofgithubers.connection

import com.akusuka.favoriteofgithubers.model.Users

data class CallbackUsers(
    val total_count: Int = 0,
    val incomplete_results: Boolean = false,
    val items: ArrayList<Users> = arrayListOf()
)