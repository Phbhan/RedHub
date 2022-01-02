package com.redhub.controller.mainscreen

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.redhub.R
import com.redhub.databinding.ActivityReadArticleBinding
import com.redhub.model.ArticleModel
import com.redhub.model.DirectorModel
import com.redhub.model.StarModel


class ReadArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadArticleBinding
    private lateinit var database: DatabaseReference
    private val storage = FirebaseStorage.getInstance()
    //star
    private lateinit var starListener: ValueEventListener
    private val starUriList = ArrayList<String>()

    private lateinit var starRecyclerView: RecyclerView
    private lateinit var list_stars : ArrayList<StarModel>
    private var starAdapter:StarAdapter?=null

    //director
    private lateinit var directorListener: ValueEventListener


    private lateinit var directorRecyclerView: RecyclerView
    private lateinit var list_directors : ArrayList<DirectorModel>
    private var directorAdapter:DirectorAdapter?=null

    val API_KEY = "AIzaSyAaTr_lxJuA81nwNG6Rg4Qht_wUihDCpb4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        var articleId: String = intent.getStringExtra("articleId").toString()


        readData(articleId)
        binding.movieReviewBtn.setOnClickListener {
            val intent: Intent = Intent(applicationContext,ReviewActivity::class.java)
            intent.putExtra("articleId",articleId)
            startActivity(intent)
        }
        database = FirebaseDatabase.getInstance().getReference("article")
        binding.movieRatingBar.setOnRatingBarChangeListener(object: RatingBar.OnRatingBarChangeListener{
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                database.child(articleId).child("rate").setValue(rating)
            }

        })
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun readData(articleId: String) {
        database = FirebaseDatabase.getInstance().getReference("article")

        database.child(articleId).get().addOnSuccessListener {
            if (it.exists()) {
                val title = it.child("title").value
                val description = it.child("description").value
                val release_date = it.child("releasedDate").value
                val genre = it.child("genre").value
                val rate = it.child("rate").value
                val posterUri = it.child("posterUri").value.toString()
                val youtubeID = it.child("youtubeID").value.toString()


               Toast.makeText(this, "Successfuly Read", Toast.LENGTH_SHORT).show()

                binding.movieTitle.text = title.toString()
                binding.movieDescription.text = description.toString()
                binding.movieReleaseDate.text= release_date.toString()
                binding.movieGenre.text = genre.toString()

                val poster = storage.getReferenceFromUrl(posterUri)
                poster.downloadUrl.addOnSuccessListener { uri ->
                    //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
                    Glide.with(this)
                        .load(uri.toString())
                        .fitCenter()
                        .into(binding.ivMoviePoster)
                }.addOnFailureListener {
                    // Handle any errors
                }

                binding.ytplayer.setOnClickListener(View.OnClickListener {
                    val intent = YouTubeStandalonePlayer.createVideoIntent(this,API_KEY,youtubeID)
                    startActivity(intent)
                })

                binding.movieRatingBar.rating = rate.toString().toFloat()



                addDirectorEventListener(database.child(articleId).child("director"))
                addStarEventListener(database.child(articleId).child("star"))

            }
        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addDirectorEventListener(dbReference: DatabaseReference) {
        // [START post_value_event_listener]
        directorListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutDirector.removeAllViews()
                for (directorSnapshot in dataSnapshot.getChildren()) {
                    val name_val = directorSnapshot.getValue().toString()

                    val inflater = LayoutInflater.from(this@ReadArticleActivity).inflate(R.layout.layout_director_list, null)
                    binding.parentLinearLayoutDirector.addView(inflater, binding.parentLinearLayoutDirector.childCount)
                    val director_name = inflater.findViewById<TextView>(R.id.director_name)
                    director_name.setText(name_val)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(directorListener)
        // [END post_value_event_listener]
    }

    private fun addStarEventListener(dbReference: DatabaseReference) {
        // [START post_value_event_listener]
        starListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutStar.removeAllViews()
                for (starSnapshot in dataSnapshot.getChildren()) {
                    val name_val = starSnapshot.child("name").getValue().toString()
                    val role_val = starSnapshot.child("role").getValue().toString()
                    val img_star = starSnapshot.child("imgUri").getValue().toString()
                    starUriList.add(img_star)

                    val inflater = LayoutInflater.from(this@ReadArticleActivity).inflate(R.layout.layout_star_list, null)
                    binding.parentLinearLayoutStar.addView(inflater, binding.parentLinearLayoutStar.childCount)
                    val star_name = inflater.findViewById<TextView>(R.id.star_name)
                    val star_role= inflater.findViewById<TextView>(R.id.star_role)
                    val iv_star_i = inflater.findViewById<ImageView>(R.id.StarImageView)


                    star_name.setText(name_val)
                    star_role.setText(role_val)
                    val poster = storage.getReferenceFromUrl(img_star)
                    poster.downloadUrl.addOnSuccessListener { uri ->
                        //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
                        Glide.with(this@ReadArticleActivity)
                            .load(uri.toString())
                            .fitCenter()
                            .into(iv_star_i)
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(starListener)
        // [END post_value_event_listener]
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
