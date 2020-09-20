package com.akusuka.githubers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.akusuka.githubers.adapter.ListReposAdapter
import com.akusuka.githubers.model.Repository
import com.akusuka.githubers.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_repository.*

private const val ARG_PARAM1 = "username"

class RepositoryFragment : Fragment() {

    private var username: String? = null
    private lateinit var adapter: ListReposAdapter


    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            RepositoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repository, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViewModel()
        showRecyclerView()
    }

    private fun loadViewModel(){
        (activity as DetailActivity).showLoading(true)
        val detailViewModel: DetailViewModel by activityViewModels()
        detailViewModel.setRepository(username)
        detailViewModel.getRepository().observe(viewLifecycleOwner, Observer { reposItems ->
            if (reposItems != null) {
                adapter.setData(reposItems)
                (activity as DetailActivity).showLoading(false)
                if(reposItems.size == 0){
                    Toast.makeText(context,
                        R.string.no_repos, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context,
                    R.string.error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecyclerView(){
        adapter = ListReposAdapter()
        adapter.notifyDataSetChanged()
        rv_repos.layoutManager = LinearLayoutManager(activity)
        rv_repos.adapter = adapter
        adapter.setOnItemClickCallback(object : ListReposAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Repository) {
                Toast.makeText(activity, data.name, Toast.LENGTH_SHORT).show()
            }
        })
    }
}