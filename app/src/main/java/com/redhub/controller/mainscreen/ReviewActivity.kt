package com.redhub.controller.mainscreen

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redhub.Helper.MyButton
import com.redhub.Helper.MySwipeHelper
import com.redhub.Listener.MyButtonClickListener
import com.redhub.R
import com.redhub.databinding.ActivityReviewBinding
import com.redhub.model.DirectorModel
import com.redhub.model.ReviewModel

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var database: DatabaseReference

    var likereference: DatabaseReference? = null
    var testclick = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvReview.setHasFixedSize(true)
        val intent = intent
        val articleId:String = intent.getStringExtra("articleId").toString()
        database = FirebaseDatabase.getInstance().getReference("article")

        var rv_position:Int = 0

        val swipe = object :MySwipeHelper(this,binding.rvReview,200)
        {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(this@ReviewActivity,"Edit",
                        30,
                        0,
                        Color.parseColor("FF3C30"),
                        object: MyButtonClickListener{
                            override fun onClick(position: Int) {
                               rv_position = position
                            }

                        })
                )
            }

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

        val user=FirebaseAuth.getInstance().currentUser
        val username = user?.displayName
        binding.currentuserName.text=username

        binding.addReviewBtn.setOnClickListener {

            saveReview(articleId)

        }

        readReview(articleId,rv_position)

    }
    private fun readReview(articleId: String,rv_position:Int)
    {
        database.child(articleId).get().addOnSuccessListener{
            if(it.exists()){
                readReviewList(articleId,rv_position)
            }
        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readReviewList(articleId: String,rv_position:Int){
        database.child(articleId).addValueEventListener(object:ValueEventListener{
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
                    val reviewId = list_reviews[rv_position].reviewId
                    val intent: Intent = Intent(applicationContext,ReviewActivity::class.java)
                    intent.putExtra("articleId",articleId)
                    intent.putExtra("reviewId",reviewId)
                    startActivity(intent)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })
    }

    private fun saveReview(articleId:String)
    {
        val current_user_name = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val review_content = binding.postReview.text.toString().trim()

        if(review_content.isEmpty()) {
            binding.postReview.error = "Please enter review"
            return
        }

        val reviewId = database.child(articleId).child("review").child(current_user_name).push().key

        val review = reviewId?.let{
            if (current_user_name != null) {
                ReviewModel(review_content)
            }
        }

        if(reviewId != null)
        {

            database.child(articleId).child("review").child(current_user_name).child(reviewId).setValue(review).addOnSuccessListener{
                binding.postReview.text?.clear()


                Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
