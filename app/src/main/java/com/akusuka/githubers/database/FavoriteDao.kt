package com.akusuka.githubers.database

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.akusuka.githubers.model.Users


@Dao
interface FavoriteDao {

    companion object {
        const val TABLE_NAME = "favorite"
        const val USERNAME = "username"
        fun fromContentValues(values: ContentValues?): Users? {
            val users = Users()
            if (values != null) {
                users.username = values.getAsString(FavoriteDao.USERNAME)
                users.avatar_url = values.getAsString("avatar_url")
            }
            return users
        }
    }

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): LiveData<List<Users>>

    @Query("SELECT EXISTS (SELECT * FROM $TABLE_NAME WHERE $USERNAME = :username)")
    fun isFavorited(username: String): LiveData<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Users)

    @Delete
    suspend fun delete(user: Users)

    //  QUERY FOR CONTENT PROVIDER

    @Query("SELECT * FROM $TABLE_NAME")
    fun selectAll(): Cursor?

    @Query("SELECT * FROM $TABLE_NAME WHERE $USERNAME = :username")
    fun checkByUsername(username: String): Cursor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Users?): Long

    @Query("DELETE FROM $TABLE_NAME WHERE $USERNAME = :username")
    fun deleteByUsername(username: String): Int
}