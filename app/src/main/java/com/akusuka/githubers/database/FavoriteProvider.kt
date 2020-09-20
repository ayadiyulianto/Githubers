package com.akusuka.githubers.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.akusuka.githubers.database.FavoriteDao.Companion.fromContentValues
import com.akusuka.githubers.model.Users


class FavoriteProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.akusuka.githubers.provider"
        private const val SCHEME = "content"
        private const val TABLE_NAME  = FavoriteDao.TABLE_NAME
        val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(TABLE_NAME)
            .build()

        private const val FAVORITE = 1
        private const val FAVORITE_ITEM = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE)
            sUriMatcher.addURI(AUTHORITY,
                "$TABLE_NAME/*",
                FAVORITE_ITEM)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor: Cursor?
        val context = context ?: return null
        val favoriteDao: FavoriteDao = AppDatabase.getDatabase(context).favoriteDao()
        cursor = when (sUriMatcher.match(uri)) {
            FAVORITE -> favoriteDao.selectAll()
            FAVORITE_ITEM -> favoriteDao.checkByUsername(uri.lastPathSegment.toString())
            else -> null
        }
        cursor?.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val context = context ?: return null
        val added: Long = when (FAVORITE) {
            sUriMatcher.match(uri) -> {
                val users: Users? = fromContentValues(values)
                AppDatabase.getDatabase(context).favoriteDao()
                    .insert(users)
            }
            else -> 0
        }
        context.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val context = context ?: return 0
        val deleted: Int = when (FAVORITE_ITEM) {
            sUriMatcher.match(uri) -> AppDatabase.getDatabase(context).favoriteDao().deleteByUsername(uri.lastPathSegment.toString())
            else -> 0
        }
        context.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }
}
