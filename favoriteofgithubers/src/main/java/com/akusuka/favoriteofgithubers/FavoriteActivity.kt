package com.akusuka.favoriteofgithubers

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.akusuka.favoriteofgithubers.adapter.ListUserAdapter
import com.akusuka.favoriteofgithubers.model.MyHelper
import com.akusuka.favoriteofgithubers.model.Users
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var adapter: ListUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        initToolbar()
        showRecyclerView()
        loadData(savedInstanceState)
    }

    private fun initToolbar() {
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
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
            val list = savedInstanceState.getParcelableArrayList<Users>(EXTRA_STATE)
            if (list != null) {
                adapter.setData(list)
            }
        }
    }

    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            showLoading(true)
            val deferredFavorites = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(MyHelper.CONTENT_URI, null, null, null, null)
                MyHelper.mapCursorToArrayList(cursor)
            }
            val favorite = deferredFavorites.await()
            showLoading(false)
            if (favorite.size > 0) {
                adapter.setData(favorite)
            } else {
                adapter.setData(ArrayList())
                val contextView = findViewById<View>(R.id.rv_users)
                Snackbar.make(contextView, R.string.empty_result, Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK") {}.show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listUsers)
    }

    private fun showSelectedUser(users: Users){
        val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USER, users)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_users -> {
                val intent = Intent(this@FavoriteActivity, MainActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home -> {
                Toast.makeText(this, getString(R.string.nav_clicked), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            progressBar.visibility = View.VISIBLE
        }else {
            progressBar.visibility = View.GONE
        }
    }
}