package com.redhub.controller.mainscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redhub.R
import com.redhub.databinding.ActivityReadArticleBinding
import com.redhub.databinding.ActivityReviewBinding
import com.redhub.model.DirectorModel
import com.redhub.model.ReviewModel

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val intent = intent
        val articleId:String = intent.getStringExtra("articleId").toString()
        database = FirebaseDatabase.getInstance().getReference("Article")
        readReview(articleId)

        val user=FirebaseAuth.getInstance().currentUser
        val username = user?.displayName
        binding.currentuserName.text=username

        binding.addReviewBtn.setOnClickListener {

                saveReview(articleId)

        }
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

    private fun saveReview(articleId:String)
    {
        val current_user_name = FirebaseAuth.getInstance().currentUser?.displayName
        val review_content = binding.postReview.text.toString().trim()

        if(review_content.isEmpty()) {
            binding.postReview.error = "Please enter review"
            return
        }

        val reviewId = database.child(articleId).child("review").push().key

        val review = reviewId?.let{
            if (current_user_name != null) {
                ReviewModel(it,current_user_name,review_content)
            }
        }

        if(reviewId != null)
        {
            database.child(articleId).child("review").child(reviewId).setValue(review).addOnSuccessListener {
                binding.postReview.text.clear()

                Toast.makeText(this,"Successfully!",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }

        }

    }
}
//Chưa like, dislike