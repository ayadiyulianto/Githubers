package com.akusuka.githubers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akusuka.githubers.R
import com.akusuka.githubers.model.Users
import com.bumptech.glide.Glide
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class ListUserAdapter: RecyclerView.Adapter<ListUserAdapter.ListViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate{

    private val listUsers = ArrayList<Users>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setData(items: ArrayList<Users>) {
        listUsers.clear()
        listUsers.addAll(items)
        listUsers.sortBy { it.username.toLowerCase(Locale.getDefault()) }
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUsers[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar_url)
            .into(holder.imgAvatar)
        holder.tvUsername.text = user.username
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(user) }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.tv_item_username)
        var imgAvatar: CircleImageView = itemView.findViewById(R.id.img_item_avatar)
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Users)
    }

    override fun onChange(position: Int): CharSequence {
        return listUsers[position].username[0].toString().toUpperCase(Locale.getDefault())
    }
}