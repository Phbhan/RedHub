package com.redhub.mainscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.redhub.databinding.ActivityReadArticleBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ReadArticleActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReadArticleBinding
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val articleId: String=""
         readData(articleId)
    }
    private fun readData(articleId:String){
        database = FirebaseDatabase.getInstance().getReference("Article")
        database.child(articleId).get().addOnSuccessListener {
            if(it.exists()){
                val title = it.child("title").value
                val description = it.child("description").value
                val release_date = it.child("release_date").value
                val gerne = it.child("gerne").value
                val director = it.child("director").value
                val star = it.child("star").value
                val num_rate = it.child("num_rate").value
                val rate = it.child("rate").value
                val poster = it.child("posterUri").value
                val youtubeID = it.child("youtubeID").value
                val review = it.child("review").value

                Toast.makeText(this,"Successfuly Read", Toast.LENGTH_SHORT).show()
                //binding.etusername.text.clear()
                binding.movieTitle.text=title.toString()


            }
        }.addOnFailureListener{

            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
}


}