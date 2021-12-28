package com.redhub.controller.mainscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redhub.R
import com.redhub.databinding.ActivityUpdateReviewBinding
import com.redhub.model.DirectorModel
import com.redhub.model.ReviewModel
import kotlinx.android.synthetic.main.layout_review_list.*

class UpdateReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateReviewBinding
    private lateinit var database: DatabaseReference

    var likereference: DatabaseReference? = null
    var testclick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = intent
        val articleId:String = intent.getStringExtra("articleId").toString()
        val reviewId:String = intent.getStringExtra("reviewId").toString()

        database = FirebaseDatabase.getInstance().getReference("Article")
        readReview(articleId)

        val user=FirebaseAuth.getInstance().currentUser
        val username = user?.displayName
        binding.currentuserName.text=username

        binding.updateReviewBtn.setOnClickListener {


            val review_content = binding.postReview.text.toString()
            updateReview(articleId,reviewId,review_content)

        }

        likereference = FirebaseDatabase.getInstance().getReference("likes")

        val options = FirebaseRecyclerOptions.Builder<ReviewModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("review"),
                ReviewModel::class.java
            )
            .build()

        val firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ReviewModel,ReviewAdapter.ViewHolder> =
        object : FirebaseRecyclerAdapter<ReviewModel,ReviewAdapter.ViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ReviewAdapter.ViewHolder {
                return ReviewAdapter.ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_review_list, parent, false)
                )
            }

            override fun onBindViewHolder(
                holder: ReviewAdapter.ViewHolder,
                position: Int,
                model: ReviewModel
            ) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val userid = firebaseUser!!.uid
                val postkey = getRef(position).key
                holder.getLikeButtonStatus(postkey, userid)
                holder.like_btn.setOnClickListener {
                    testclick = true
                    likereference!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (testclick == true) {
                                testclick = if (snapshot.child(postkey!!).hasChild(userid)) {
                                    likereference!!.child(postkey).child(userid).removeValue()
                                    false
                                } else {
                                    likereference!!.child(postkey).child(userid).setValue(true)
                                    false
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

        }
        firebaseRecyclerAdapter.startListening()
    }
    private fun readReview(articleId: String)
    {
        database.child(articleId).get().addOnSuccessListener{
            if(it.exists()){
                readReviewList()
            }
        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readReviewList(){
        database.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)  {
                val list_reviews = ArrayList<ReviewModel>()
                for (review in snapshot.child("review").children)
                {
                    val model = review.getValue(DirectorModel::class.java)
                    list_reviews.add(model as ReviewModel)
                }
                if(list_reviews.size >0)
                {
                    val adapter = ReviewAdapter(list_reviews)
                    binding.rvReview.adapter = adapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })
    }

    private fun updateReview(articleId: String,reviewId:String,review_content:String)
    {

        val review = mapOf<String, String>(
            "review_content" to review_content
        )
        val current_user_name = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        database.child(articleId).child("review").child(current_user_name).child(reviewId).updateChildren(review).addOnSuccessListener {
            binding.postReview.text!!.clear()
            Toast.makeText(this,"Updated!",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"Fail to edit",Toast.LENGTH_SHORT).show()
        }

    }
}
