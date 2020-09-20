package com.akusuka.favoriteofgithubers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akusuka.favoriteofgithubers.R
import com.akusuka.favoriteofgithubers.model.Repository
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListReposAdapter: RecyclerView.Adapter<ListReposAdapter.ListViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate{

    private val listRepos = ArrayList<Repository>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setData(items: ArrayList<Repository>) {
        listRepos.clear()
        listRepos.addAll(items)
        listRepos.sortBy { it.name.toLowerCase(Locale.getDefault()) }
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_repos, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listRepos.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val repository = listRepos[position]
        holder.tvName.text = repository.name
        holder.tvDescription.text = repository.description
        holder.tvLanguage.text = repository.language
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val formater = SimpleDateFormat("dd MMM yy", Locale.getDefault())
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val formated = formater.format(parser.parse(repository.updated_at))
        holder.tvUpdateAt.text = formated
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(repository) }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        var tvLanguage: TextView = itemView.findViewById(R.id.tv_language)
        var tvUpdateAt: TextView = itemView.findViewById(R.id.tv_update_at)
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Repository)
    }

    override fun onChange(position: Int): CharSequence {
        return listRepos[position].name[0].toString().toUpperCase(Locale.getDefault())
    }
}