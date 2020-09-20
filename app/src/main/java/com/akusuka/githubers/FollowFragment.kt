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
import com.akusuka.githubers.adapter.ListUserAdapter
import com.akusuka.githubers.model.Users
import com.akusuka.githubers.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_follow.*


private const val ARG_PARAM1 = "username"
private const val ARG_PARAM2 = "type"

class FollowFragment : Fragment() {

    private var username: String? = null
    private var type: Int? = null
    private lateinit var adapter: ListUserAdapter

    companion object {
        @JvmStatic
        fun newInstance(username: String, type: Int) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, username)
                    putInt(ARG_PARAM2, type)
                }
            }
        const val FOLLOWING = R.string.following
        const val FOLLOWERS = R.string.followers
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_PARAM1)
            type = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViewModel()
        showRecyclerView()
    }

    private fun loadViewModel(){
        (activity as DetailActivity).showLoading(true)
        val detailViewModel: DetailViewModel by activityViewModels()
        if (type == FOLLOWING) {
            detailViewModel.setFollowing(username)
            detailViewModel.getFollowing().observe(viewLifecycleOwner, Observer { userItems ->
                if (userItems != null) {
                    adapter.setData(userItems)
                    (activity as DetailActivity).showLoading(false)
                    if(userItems.size == 0){
                        Toast.makeText(context,
                            R.string.no_following, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,
                        R.string.error, Toast.LENGTH_SHORT).show()
                }
            })
        }
        else if (type == FOLLOWERS) {
            detailViewModel.setFollowers(username)
            detailViewModel.getFollowers().observe(viewLifecycleOwner, Observer { userItems ->
                if (userItems != null) {
                    adapter.setData(userItems)
                    (activity as DetailActivity).showLoading(false)
                    if(userItems.size == 0){
                        Toast.makeText(context,
                            R.string.no_followers, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,
                        R.string.error, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showRecyclerView(){
        adapter = ListUserAdapter()
        adapter.notifyDataSetChanged()
        rv_users.layoutManager = LinearLayoutManager(activity)
        rv_users.adapter = adapter
        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Users) {
                Toast.makeText(activity, data.username, Toast.LENGTH_SHORT).show()
            }
        })
    }
}