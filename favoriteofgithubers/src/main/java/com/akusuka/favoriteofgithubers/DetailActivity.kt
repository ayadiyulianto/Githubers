package com.akusuka.favoriteofgithubers

import android.content.ContentValues
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akusuka.favoriteofgithubers.adapter.DetailPagerAdapter
import com.akusuka.favoriteofgithubers.model.MyHelper
import com.akusuka.favoriteofgithubers.model.User
import com.akusuka.favoriteofgithubers.model.Users
import com.akusuka.favoriteofgithubers.viewmodel.DetailViewModel
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        const val EXTRA_USER = "extra_user"
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var user: User
    private var isFavorited: Boolean = false
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var uriWithId: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intentUser = intent.getParcelableExtra<Users>(EXTRA_USER)
        loadViewModel(intentUser)
        initToolbar()
        loadData(savedInstanceState)

        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val detailPagerAdapter =
            DetailPagerAdapter(
                this,
                supportFragmentManager,
                intentUser.username
            )
        view_pager.adapter = detailPagerAdapter
        tabs.setupWithViewPager(view_pager)

        ic_favorite.setOnClickListener(this)
        ic_message.setOnClickListener(this)
    }

    private fun loadViewModel(intentUsers: Users?){
        showLoading(true)
        user = User(intentUsers!!.username)
        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).
            get(DetailViewModel::class.java)
        detailViewModel.setUser(intentUsers.username)
        detailViewModel.getUser().observe(this, Observer { userItem ->
            if (userItem != null) {
                user = userItem
                showUserDetail()
                showLoading(false)
            }
        })

        uriWithId = Uri.parse(MyHelper.CONTENT_URI.toString() + "/" + intentUsers.username)
    }

    private fun initToolbar(){
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var isShow = true
        var scrollRange = -1
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1){
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0){
                cToolbar.title = user.name
                cToolbar.setCollapsedTitleTextColor(Color.WHITE)
                isShow = true
            } else if (isShow){
                cToolbar.title = " "
                isShow = false
            }
        })
    }

    private fun loadData(savedInstanceState: Bundle?){
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadFavoriteAsync()
            }
        }

        contentResolver.registerContentObserver(MyHelper.CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadFavoriteAsync()
        } else {
            isFavorited = savedInstanceState.getBoolean(EXTRA_STATE, false)
        }
    }

    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            val deferredFavorites = async(Dispatchers.IO) {
                val cursor = contentResolver.query(uriWithId, null, null, null, null)
                MyHelper.mapCursorToObject(cursor)
            }
            val favorited = deferredFavorites.await()
            Log.d("isFavorited", favorited.username + ", user : " +user.username)
            isFavorited = favorited.username == user.username
            showFavoriteIcon()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_STATE, isFavorited)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    private fun showUserDetail(){
        Glide.with(this)
            .load(user.avatar_url)
            .into(img_avatar)
        tv_name.text = user.name
        tv_username.text = user.username
        val stats: String = String.format(resources.getString(R.string.stats), user.following, user.followers, user.public_repos)
        tv_stats.text = stats
    }

    private fun showFavoriteIcon(){
        if(isFavorited) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_24, applicationContext.theme))
            } else {
                @Suppress("DEPRECATION")
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_24))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24, applicationContext.theme))
            } else {
                @Suppress("DEPRECATION")
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.ic_message -> {
                Toast.makeText(this, getString(R.string.chat), Toast.LENGTH_SHORT).show()
            }
            R.id.ic_favorite -> {
                if(isFavorited) {
                    contentResolver.delete(uriWithId, null, null)
                    Toast.makeText(this, getString(R.string.removed), Toast.LENGTH_SHORT).show()
                } else {
                    val values = ContentValues()
                    values.put("username", user.username)
                    values.put("avatar_url", user.avatar_url)
                    contentResolver.insert(MyHelper.CONTENT_URI, values)
                    Toast.makeText(this, getString(R.string.added), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun showLoading(state: Boolean) {
        if(state) {
            progressBar.visibility = View.VISIBLE
        }else {
            progressBar.visibility = View.GONE
        }
    }

}
