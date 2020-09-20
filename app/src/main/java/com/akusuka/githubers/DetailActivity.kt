package com.akusuka.githubers

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akusuka.githubers.adapter.DetailPagerAdapter
import com.akusuka.githubers.model.User
import com.akusuka.githubers.model.Users
import com.akusuka.githubers.viewmodel.DetailViewModel
import com.akusuka.githubers.viewmodel.FavoriteViewModel
import com.akusuka.githubers.widget.MyFavoriteStackWidget
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        const val EXTRA_USER = "extra_user"
    }

    private lateinit var user: User
    private var isFavorited: Boolean = false
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intentUser = intent.getParcelableExtra<Users>(EXTRA_USER)
        loadViewModel(intentUser)
        initToolbar()

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
        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).
            get(DetailViewModel::class.java)
        detailViewModel.setUser(intentUsers?.username)
        detailViewModel.getUser().observe(this, Observer { userItem ->
            if (userItem != null) {
                user = userItem
                showUserDetail()
                showLoading(false)
            }
        })

        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        favoriteViewModel.checkFavorite(intentUsers!!.username)
        favoriteViewModel.isFavorite.observe(this, Observer { check ->
            isFavorited = check
            showFavoriteIcon()
        })

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
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_24));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24, applicationContext.theme))
            } else {
                @Suppress("DEPRECATION")
                ic_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.ic_message -> {
                Toast.makeText(this, getString(R.string.chat), Toast.LENGTH_SHORT).show()
            }
            R.id.ic_favorite -> {
                val users = Users(user.username, user.avatar_url)
                if(isFavorited) {
                    favoriteViewModel.delete(users)
                    Toast.makeText(this, getString(R.string.removed), Toast.LENGTH_SHORT).show()
                } else {
                    favoriteViewModel.insert(users)
                    Toast.makeText(this, getString(R.string.added), Toast.LENGTH_SHORT).show()
                }
                // update appwidget
                MyFavoriteStackWidget.sendRefreshBroadcast(this)
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
