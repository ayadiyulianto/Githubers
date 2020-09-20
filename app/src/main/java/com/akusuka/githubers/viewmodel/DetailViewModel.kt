package com.akusuka.githubers.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akusuka.githubers.connection.GitHubService
import com.akusuka.githubers.connection.RestAdapter
import com.akusuka.githubers.model.Repository
import com.akusuka.githubers.model.User
import com.akusuka.githubers.model.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val gitHubService: GitHubService =
        RestAdapter.gitHubService
    private val user = MutableLiveData<User>()
    private val listFollowing = MutableLiveData<ArrayList<Users>>()
    private val listFollowers = MutableLiveData<ArrayList<Users>>()
    private val listRepository = MutableLiveData<ArrayList<Repository>>()

    fun setUser(username: String?){
        val callbackCall: Call<User?>? = gitHubService.getUser(username)
        callbackCall?.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>?, response: Response<User?>) {
                val resp: User? = response.body()
                if (resp != null) {
                    user.postValue(resp)
                } else {
                    Log.d("onResponse User", "Not Found")
                }
            }
            override fun onFailure(call: Call<User?>, t: Throwable?) {
                Log.d("onFailure User", t.toString())
            }
        })
    }

    fun setFollowing(username: String?){
        val callbackCall: Call<ArrayList<Users>?>? = gitHubService.getFollowing(username)
        callbackCall?.enqueue(object : Callback<ArrayList<Users>?> {
            override fun onResponse(call: Call<ArrayList<Users>?>?, response: Response<ArrayList<Users>?>) {
                val resp: ArrayList<Users>? = response.body()
                if (resp != null) {
                    listFollowing.postValue(resp)
                } else {
                    Log.d("onResponse User", "Not Found")
                }
            }
            override fun onFailure(call: Call<ArrayList<Users>?>, t: Throwable?) {
                Log.d("onFailure User", t.toString())
            }
        })
    }

    fun setFollowers(username: String?){
        val callbackCall: Call<ArrayList<Users>?>? = gitHubService.getFollowers(username)
        callbackCall?.enqueue(object : Callback<ArrayList<Users>?> {
            override fun onResponse(call: Call<ArrayList<Users>?>?, response: Response<ArrayList<Users>?>) {
                val resp: ArrayList<Users>? = response.body()
                if (resp != null) {
                    listFollowers.postValue(resp)
                } else {
                    Log.d("onResponse User", "Not Found")
                }
            }
            override fun onFailure(call: Call<ArrayList<Users>?>, t: Throwable?) {
                Log.d("onFailure User", t.toString())
            }
        })
    }

    fun setRepository(username: String?){
        val callbackCall: Call<ArrayList<Repository>?>? = gitHubService.getRepository(username)
        callbackCall?.enqueue(object : Callback<ArrayList<Repository>?> {
            override fun onResponse(call: Call<ArrayList<Repository>?>?, response: Response<ArrayList<Repository>?>) {
                val resp: ArrayList<Repository>? = response.body()
                if (resp != null) {
                    listRepository.postValue(resp)
                } else {
                    Log.d("onResponse User", "Not Found")
                }
            }
            override fun onFailure(call: Call<ArrayList<Repository>?>, t: Throwable?) {
                Log.d("onFailure User", t.toString())
            }
        })
    }

    fun getUser(): LiveData<User> {
        return user
    }

    fun getFollowing(): LiveData<ArrayList<Users>> {
        return listFollowing
    }

    fun getFollowers(): LiveData<ArrayList<Users>> {
        return listFollowers
    }

    fun getRepository(): LiveData<ArrayList<Repository>> {
        return listRepository
    }
}