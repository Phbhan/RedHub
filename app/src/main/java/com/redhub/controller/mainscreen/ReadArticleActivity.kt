package com.redhub.controller.mainscreen

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.redhub.R
import com.redhub.databinding.ActivityReadArticleBinding
import com.redhub.model.DirectorModel
import com.redhub.model.StarModel


class ReadArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadArticleBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val articleId: String = ""
        database = FirebaseDatabase.getInstance().getReference("Article")
        readData(articleId)

    }

    private fun readData(articleId: String) {

        database.child(articleId).get().addOnSuccessListener {
            if (it.exists()) {
                val title = it.child("title").value
                val description = it.child("description").value
                val release_date = it.child("release_date").value
                val gerne = it.child("gerne").value
                val num_rate = it.child("num_rate").value
                val rate = it.child("rate").value
                val poster = it.child("posterUri").value
                val youtubeID = it.child("youtubeID").value




                Toast.makeText(this, "Successfuly Read", Toast.LENGTH_SHORT).show()

                binding.movieTitle.text = title.toString()
                binding.movieDescription.text = description.toString()
                binding.movieReleaseDate.text= release_date.toString()
                binding.movieGerne.text = gerne.toString()
                binding.movieNumRate.text=num_rate.toString()
                binding.movieRating.text = rate.toString()
                //binding.ivMoviePoster.setImageURI(Uri.parse(poster.toString()))
                Glide.with(this)
                    .load(Uri.parse(poster.toString()))
                    .into(binding.ivMoviePoster)

                readDirectorsList()
                readStarList()

            }
        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun readDirectorsList()
    {
        database.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list_directors = ArrayList<DirectorModel>()
                for (director in snapshot.child("director").children)
                {
                    val model = director.getValue(DirectorModel::class.java)
                    list_directors.add(model as DirectorModel)
                }
                if(list_directors.size >0)
                {
                    val adapter = DirectorAdapter(list_directors)
                    binding.recyclerviewDirector.adapter = adapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })
    }

    private fun readStarList()
    {
        database.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list_stars = ArrayList<StarModel>()
                for (star in snapshot.child("star").children)
                {
                    val model = star.getValue(StarModel::class.java)
                    list_stars.add(model as StarModel)
                }
                if(list_stars.size >0)
                {
                    val adapter = StarAdapter(list_stars)
                    binding.recyclerviewStar.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })
    }
}
