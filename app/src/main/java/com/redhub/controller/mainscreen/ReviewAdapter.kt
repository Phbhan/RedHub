package com.redhub.controller.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redhub.R
import com.redhub.model.DirectorModel
import com.redhub.model.ReviewModel
import kotlinx.android.synthetic.main.layout_director_list.view.*
import kotlinx.android.synthetic.main.layout_review_list.view.*

class ReviewAdapter(private val list_reviews: ArrayList<ReviewModel>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var name = itemView.review_username
        val content = itemView.review_content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_review_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list_reviews[position].username
        holder.content.text=list_reviews[position].review_content
    }

    override fun getItemCount(): Int {
        return list_reviews.size
    }
}