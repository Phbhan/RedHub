package com.redhub.controller.mainscreen

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.R
import com.redhub.databinding.ActivityReviewBinding
import com.redhub.model.ReviewModel

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var database: DatabaseReference
    private lateinit var reviewListener: ValueEventListener
    private lateinit var myRef: DatabaseReference
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val articleId:String = intent.getStringExtra("articleId").toString()


        val user=FirebaseAuth.getInstance().currentUser
        val username = user?.displayName

        binding.currentuserName.text=username

        binding.addReviewBtn.setOnClickListener {
            saveReview(articleId)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java)
            startActivity(intent)
        }

        val avatarUrl = user?.photoUrl.toString()
        val avatar = storage.getReferenceFromUrl(avatarUrl)
        avatar.downloadUrl.addOnSuccessListener { uri ->
            //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
            Glide.with(this)
                .load(uri.toString())
                .into(binding.ivAvatar)
        }.addOnFailureListener {
            // Handle any errors
        }

        val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
        myRef = database.getReference("article").child(articleId).child("review")
        addReviewEventListener(myRef,articleId)

    }
    private fun saveReview(articleId:String)
    {
        database = FirebaseDatabase.getInstance().getReference("article").child(articleId).child("review")
        val current_user_name = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val avatar = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        val review_content = binding.postReview.text.toString().trim()

        if(review_content.isEmpty()) {
            binding.postReview.error = "Please enter review"
            return
        }

        val reviewId = database.push().key
        if(reviewId != null)
        {
            database.child(reviewId).child("username").setValue(current_user_name)
            database.child(reviewId).child("avatar").setValue(avatar)
            database.child(reviewId).child("content").setValue(review_content).addOnSuccessListener{
                binding.postReview.text?.clear()

                Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun addReviewRow(review: ReviewModel, reviewId: String,articleId: String)
    {
        val inflater = LayoutInflater.from(this).inflate(R.layout.layout_review_list, null)
        binding.parentLinearLayoutReview.addView(inflater, binding.parentLinearLayoutReview.childCount)
        val review_username= inflater.findViewById<TextView>(R.id.review_username)
        review_username.text = review.username
        val review_content= inflater.findViewById<TextView>(R.id.review_content)
        review_content.text = review.review_content
        val iv_avatar = inflater.findViewById<ImageView>(R.id.ivAvatar)

        val avatar = storage.getReferenceFromUrl(review.avatar)
        avatar.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri.toString())
                .override(30,30)
                .into(iv_avatar)
        }.addOnFailureListener {
            // Handle any errors
        }
        inflater.setOnClickListener {
            val user=FirebaseAuth.getInstance().currentUser
            val username = user?.displayName
            if(username == review.username)
            {
                val intent = Intent(this, UpdateReviewActivity::class.java)
                intent.putExtra("reviewId", reviewId)
                intent.putExtra("articleId",articleId)
                myRef.removeEventListener(reviewListener)
                startActivity(intent)
            }
        }

    }
    private fun addReviewEventListener(reviewReference: DatabaseReference,articleId: String) {
        // [START post_value_event_listener]
        reviewListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutReview.removeAllViews()
                for (reviewSnapshot in dataSnapshot.getChildren()) {
                    val username = reviewSnapshot.child("username").value.toString()
                    val avatar = reviewSnapshot.child("avatar").value.toString()
                    val content = reviewSnapshot.child("content").value.toString()
                    val review = ReviewModel(username,avatar,content)
                    val reviewId = reviewSnapshot.key
                    if (reviewId != null) {
                        addReviewRow(review, reviewId,articleId)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        reviewReference.addValueEventListener(reviewListener)
        // [END post_value_event_listener]
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
