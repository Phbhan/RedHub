package com.redhub.controller.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redhub.R
import com.google.firebase.database.FirebaseDatabase
import com.redhub.model.ReviewModel
import kotlinx.android.synthetic.main.layout_director_list.view.*
import kotlinx.android.synthetic.main.layout_review_list.view.*

class ReviewAdapter(private val list_reviews: ArrayList<ReviewModel>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var name = itemView.review_username
        val content = itemView.review_content
        val like_text = itemView.like_text
        val like_btn = itemView.like_btn

        var likereference: DatabaseReference?=null

        fun getLikeButtonStatus(postkey: String?, userId: String?)
        {
            likereference = FirebaseDatabase.getInstance().getReference("likes")
            likereference!!.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(postkey!!).hasChild(userId!!)) {
                        val likecount = snapshot.child(postkey).childrenCount.toInt()
                        like_text.text = "$likecount likes"
                        //like_btn.setImageResource(R.drawable.ic_baseline_favorite_24)
                    } else {
                        val likecount = snapshot.child(postkey).childrenCount.toInt()
                        like_text.text = "$likecount likes"
                        //like_btn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
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