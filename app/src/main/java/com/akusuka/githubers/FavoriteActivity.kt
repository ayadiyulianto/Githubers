package com.akusuka.githubers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akusuka.githubers.adapter.ListUserAdapter
import com.akusuka.githubers.model.Users
import com.akusuka.githubers.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {

    private lateinit var adapter: ListUserAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        initToolbar()
        showRecyclerView()
        loadVieModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.title = getString(R.string.favorite)
    }

    private fun showRecyclerView(){
        adapter = ListUserAdapter()
        adapter.notifyDataSetChanged()
        rv_users.layoutManager = LinearLayoutManager(this)
        rv_users.adapter = adapter
        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Users) {
                showSelectedUser(data)
            }
        })
    }

    private fun loadVieModel(){
        showLoading(true)
        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        favoriteViewModel.listFavorite.observe(this, Observer { favorites ->
            if (favorites != null) {
                adapter.setData(ArrayList(favorites))
                showLoading(false)
                if(favorites.size == 0) {
                    val contextView = findViewById<View>(R.id.rv_users)
                    Snackbar.make(contextView, R.string.empty_result, Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK") {}.show()
                }
            } else {
                val contextView = findViewById<View>(R.id.rv_users)
                Snackbar.make(contextView, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK") {}.show()
            }
        })
    }

    private fun showSelectedUser(users: Users){
        val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USER, users)
        startActivity(intent)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            progressBar.visibility = View.VISIBLE
        }else {
            progressBar.visibility = View.GONE
        }
    }
}