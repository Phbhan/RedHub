package com.redhub.controller.mainscreen

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.database.*
import com.redhub.R
import com.redhub.databinding.ActivityReadArticleBinding
import com.redhub.model.ArticleModel
import com.redhub.model.DirectorModel
import com.redhub.model.StarModel


class ReadArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadArticleBinding
    private lateinit var database: DatabaseReference
    //star
    private lateinit var starRecyclerView: RecyclerView
    private lateinit var list_stars : ArrayList<StarModel>
    private var starAdapter:StarAdapter?=null

    //director
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
        //director
        directorRecyclerView = binding.recyclerviewDirector
        directorRecyclerView.layoutManager= LinearLayoutManager(this)
        directorRecyclerView.setHasFixedSize(true)
        list_directors = arrayListOf<DirectorModel>()
        //star
        starRecyclerView = binding.recyclerviewStar
        starRecyclerView.layoutManager= LinearLayoutManager(this)
        starRecyclerView.setHasFixedSize(true)
        list_stars = arrayListOf<StarModel>()


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
                val poster = it.child("posterUri").value
                val youtubeID = it.child("youtubeID").value.toString()


               Toast.makeText(this, "Successfuly Read", Toast.LENGTH_SHORT).show()

                binding.movieTitle.text = title.toString()
                binding.movieDescription.text = description.toString()
                binding.movieReleaseDate.text= release_date.toString()
                binding.movieGenre.text = genre.toString()


                Glide.with(this)
                    .load(poster.toString())
                    .fitCenter()
                    .into(binding.ivMoviePoster)
                binding.ytplayer.setOnClickListener(View.OnClickListener {
                    val intent = YouTubeStandalonePlayer.createVideoIntent(this,API_KEY,youtubeID)
                    startActivity(intent)
                })

                binding.movieRatingBar.rating = rate.toString().toFloat()

                //database.child(articleId).child("rate").addValueEventListener(object:ValueEventListener{
                //    override fun onDataChange(snapshot: DataSnapshot) {
                //        if(snapshot?.value != null){
                //            val rating : Float = snapshot.value.toString().toFloat()
                //            binding.movieRatingBar.rating = rating
                //        }
                //    }
////
                //    override fun onCancelled(error: DatabaseError) {
////
                //    }
////
                //})

                //readDirectorsList(articleId)
                //readStarList(articleId)

            }
        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun readDirectorsList(articleId: String) {
        database = FirebaseDatabase.getInstance().getReference("article").child(articleId).child("director")
        database.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (director in snapshot.children)
                {
                    val model = director.getValue(DirectorModel::class.java)
                    if (model != null) {
                        list_directors.add(model)
                    }
                }
                if(list_directors.size > 0)
                {
                    directorAdapter = DirectorAdapter(list_directors)
                    directorRecyclerView.adapter = directorAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }
        })
    }

    private fun readStarList(articleId: String)
    {
        database = FirebaseDatabase.getInstance().getReference("article")
        database.child(articleId).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (star in snapshot.child("star").children)
                {
                    val model = star.getValue(StarModel::class.java)
                    list_stars.add(model!!)
                }
                if(list_stars.size > 0)
                {
                    starAdapter = StarAdapter(list_stars)
                    starRecyclerView.adapter = starAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
