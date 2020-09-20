package com.akusuka.favoriteofgithubers.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akusuka.favoriteofgithubers.connection.CallbackUsers
import com.akusuka.favoriteofgithubers.connection.GitHubService
import com.akusuka.favoriteofgithubers.connection.RestAdapter
import com.akusuka.favoriteofgithubers.model.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel : ViewModel() {

    private val gitHubService: GitHubService = RestAdapter.gitHubService
    private val listUsers: MutableLiveData<ArrayList<Users>> = getRandomUsers()

    private fun getRandomUsers(): MutableLiveData<ArrayList<Users>>{
        val random = MutableLiveData<ArrayList<Users>>()
        val callbackCall: Call<ArrayList<Users>?>? = gitHubService.getRandomUsers()
        callbackCall?.enqueue(object : Callback<ArrayList<Users>?> {
            override fun onResponse(call: Call<ArrayList<Users>?>?, response: Response<ArrayList<Users>?>) {
                val resp: ArrayList<Users>? = response.body()
                if (resp != null) {
                    random.postValue(resp)
                } else {
                    Log.d("onResponse", "Not Found")
                }
            }
            override fun onFailure(call: Call<ArrayList<Users>?>, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }
        })
        return random
    }

    fun searchUsers(query: String) {
        val callbackCall: Call<CallbackUsers?>? = gitHubService.getUsers(query)
        callbackCall?.enqueue(object : Callback<CallbackUsers?> {
            override fun onResponse(call: Call<CallbackUsers?>?, response: Response<CallbackUsers?>) {
                val resp: CallbackUsers? = response.body()
                if (resp != null) {
                    listUsers.postValue(resp.items)
                } else {
                    Log.d("onResponse", "Not Found")
                }
            }
            override fun onFailure(call: Call<CallbackUsers?>, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }
        })
    }

    fun getUsers(): LiveData<ArrayList<Users>> {
        return listUsers
    }

}