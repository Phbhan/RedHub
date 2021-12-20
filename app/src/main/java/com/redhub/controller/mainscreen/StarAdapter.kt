package com.redhub.controller.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView

import com.redhub.R
import com.redhub.model.StarModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_director_list.view.*
import kotlinx.android.synthetic.main.layout_star_list.view.*

class StarAdapter( private val list_stars: ArrayList<StarModel>) : RecyclerView.Adapter<StarAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var name = itemView.star_name
        var role = itemView.star_role
        val imguri : ImageView=itemView.StarImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_star_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list_stars[position].name
        holder.role.text=list_stars[position].role
        Picasso.get()
            .load(list_stars[position].imgUri)
            .into(holder.imguri)
    }

    override fun getItemCount(): Int {
        return list_stars.size
    }
}