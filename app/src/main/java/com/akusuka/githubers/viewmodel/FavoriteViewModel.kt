package com.akusuka.githubers.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.akusuka.githubers.database.AppDatabase
import com.akusuka.githubers.database.FavoriteDao
import com.akusuka.githubers.model.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application): AndroidViewModel(application) {

    private val favoriteDao: FavoriteDao = AppDatabase.getDatabase(application).favoriteDao()
    val listFavorite: LiveData<List<Users>> = favoriteDao.getAll()

    private val mutableFavorite: MutableLiveData<String> = MutableLiveData()
    val isFavorite: LiveData<Boolean> = Transformations.switchMap(mutableFavorite) { param->
        favoriteDao.isFavorited(param)
    }

    fun checkFavorite(username: String) {
        mutableFavorite.value = username
    }

    fun insert(favorite: Users) = viewModelScope.launch(Dispatchers.IO) {
        favoriteDao.insert(favorite)
    }

    fun delete(favorite: Users) = viewModelScope.launch(Dispatchers.IO) {
        favoriteDao.delete(favorite)
    }

}