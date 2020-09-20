package com.akusuka.favoriteofgithubers.model

import android.database.Cursor
import android.net.Uri

object MyHelper {
    
    private const val AUTHORITY = "com.akusuka.githubers.provider"
    private const val SCHEME = "content"
    private const val TABLE_NAME  = "favorite"
    val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
        .authority(AUTHORITY)
        .appendPath(TABLE_NAME)
        .build()
    
    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<Users> {
        val usersList = ArrayList<Users>()

        cursor?.apply {
            while (moveToNext()) {
                val username = getString(getColumnIndexOrThrow("username"))
                val avatar_url = getString(getColumnIndexOrThrow("avatar_url"))
                usersList.add(Users(username, avatar_url))
            }
        }
        return usersList
    }

    fun mapCursorToObject(usersCursor: Cursor?): Users {
        var users = Users()
        usersCursor?.apply {
            if(moveToFirst() && count>0) {
                val username = getString(getColumnIndexOrThrow("username"))
                val avatar_url = getString(getColumnIndexOrThrow("avatar_url"))
                users = Users(username, avatar_url)
            }
            close()
        }
        return users
    }
}