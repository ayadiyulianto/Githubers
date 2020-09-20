package com.akusuka.githubers

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akusuka.githubers.adapter.ListUserAdapter
import com.akusuka.githubers.model.Users
import com.akusuka.githubers.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ListUserAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        showRecyclerView()
        loadVieModel()
    }

    private fun initToolbar(){
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        val bg = searchView.findViewById(androidx.appcompat.R.id.search_plate) as View
        bg.background = null
        searchView.setOnClickListener { searchView.isIconified = false } // remove underline
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    Toast.makeText(this@MainActivity, getString(R.string.query_empty), Toast.LENGTH_SHORT).show()
                }else {
                    showLoading(true)
                    mainViewModel.searchUsers(query)
                    searchView.clearFocus()
                    Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
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
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).
            get(MainViewModel::class.java)
        mainViewModel.getUsers().observe(this, Observer { userItems ->
            if (userItems != null) {
                adapter.setData(userItems)
                showLoading(false)
                if(userItems.size == 0) {
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
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USER, users)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_favorite -> {
                val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_my_profile -> {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                val users = Users("ayadiyulianto")
                intent.putExtra(DetailActivity.EXTRA_USER, users)
                startActivity(intent)
            }
            R.id.menu_setting -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
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
