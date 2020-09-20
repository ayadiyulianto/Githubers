package com.akusuka.githubers.connection

import com.akusuka.githubers.model.Users

data class CallbackUsers(
    val total_count: Int = 0,
    val incomplete_results: Boolean = false,
    val items: ArrayList<Users> = arrayListOf()
)