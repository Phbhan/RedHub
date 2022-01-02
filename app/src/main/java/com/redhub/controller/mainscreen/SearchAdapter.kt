package com.redhub.controller.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.redhub.R
import com.redhub.model.ArticleModel
import com.redhub.model.StarModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_search_list.view.*
import kotlinx.android.synthetic.main.layout_star_list.view.*
import java.io.File

class SearchAdapter( private val list_search: ArrayList<ArticleModel>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    private  lateinit var  mListener: onItemClickListener
    private val storage = FirebaseStorage.getInstance()

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }
    class ViewHolder(itemView: View,listener: onItemClickListener): RecyclerView.ViewHolder(itemView)
    {
        val title : TextView
        val rate : TextView
        val imguri : ImageView
        init{
            title = itemView.movie_title
            rate = itemView.movie_rate
            imguri =itemView.ArticleImageView
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_search_list,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list_search[position].title
        holder.rate.text = list_search[position].rates.toString()

        val poster = storage.getReferenceFromUrl(list_search[position].posterUri)
        poster.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri.toString())
                .into(holder.imguri)
        }.addOnFailureListener {
            // Handle any errors
        }
        //Picasso.get()
        //    .load(list_search[position].posterUri)
        //    .into(holder.imguri)
//
    }

    override fun getItemCount(): Int {
        return list_search.size
    }

}