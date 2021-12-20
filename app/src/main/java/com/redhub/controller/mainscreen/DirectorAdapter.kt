package com.redhub.controller.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redhub.R
import com.redhub.model.DirectorModel
import kotlinx.android.synthetic.main.layout_director_list.view.*

class DirectorAdapter(private val list_directors: ArrayList<DirectorModel>) : RecyclerView.Adapter<DirectorAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var name = itemView.director_name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_director_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list_directors[position].name
    }

    override fun getItemCount(): Int {
        return list_directors.size
    }
}