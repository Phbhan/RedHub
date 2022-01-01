package com.redhub.controller.mainscreen

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.R
import com.redhub.controller.article.EditArticleActivity
import com.redhub.controller.article.ManageArticleActivity
import com.redhub.controller.profile.ViewProfileActivity
import com.redhub.databinding.ActivityMainScreenBinding
import com.redhub.model.BriefArticleModel
import com.squareup.picasso.Picasso

class MainScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    private val storage = FirebaseStorage.getInstance()
    private lateinit var articleListener: ValueEventListener
    private lateinit var myRef: DatabaseReference

    private val user = Firebase.auth.currentUser

    var dataAdmin: DatabaseReference
    init{
        dataAdmin = FirebaseDatabase.getInstance().reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
        myRef = database.getReference("article")
        addArticleEventListener(myRef)


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigasjonen)
    }
    private fun addArticleRow(article: BriefArticleModel, articleID: String)
    {
        val inflater = LayoutInflater.from(this).inflate(R.layout.article_main_row, null)
        binding.parentLinearLayoutArticle.addView(inflater, binding.parentLinearLayoutArticle.childCount)
        val tv_title= inflater.findViewById<TextView>(R.id.tv_title)
        tv_title.text = article.title
        val tv_rate= inflater.findViewById<TextView>(R.id.tv_rate)
        tv_rate.text = article.rates.toString()
        val iv_poster = inflater.findViewById<ImageView>(R.id.iv_poster)

        Glide.with(this)
            .load(article.posterUri)
            .override(378 , 297)
            .into(iv_poster)
        //val poster = storage.getReferenceFromUrl(article.posterUri)
        //poster.downloadUrl.addOnSuccessListener { uri ->
        //    //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
        //    Glide.with(this)
        //        .load(uri.toString())
        //        .override(378 , 297)
        //        .into(iv_poster)
        //}.addOnFailureListener {
            // Handle any errors
        //}
        inflater.setOnClickListener {
            val intent = Intent(this, ReadArticleActivity::class.java)
            intent.putExtra("articleId", articleID)
            Log.d("Han", articleID)
            myRef.removeEventListener(articleListener)
            startActivity(intent)
        }

    }
    private fun addArticleEventListener(articleReference: DatabaseReference) {
        // [START post_value_event_listener]
        articleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutArticle.removeAllViews()
                for (articleSnapshot in dataSnapshot.getChildren()) {
                    val title = articleSnapshot.child("title").value.toString()
                    val posterUri = articleSnapshot.child("posterUri").value.toString()
                    val rate = articleSnapshot.child("rate").value.toString().toFloat()
                    val briefarticle = BriefArticleModel(title, posterUri, rate)
                    val articleId = articleSnapshot.key
                    if (articleId != null) {
                        addArticleRow(briefarticle, articleId)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        articleReference.addValueEventListener(articleListener)
        // [END post_value_event_listener]
    }

    private val navigasjonen = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.ic_home -> {
                return@OnNavigationItemSelectedListener false
            }

            R.id.ic_profile -> {
                val intent = Intent(this@MainScreenActivity, ViewProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.ic_save -> {
                var adminValue: String
                var authNow: String

                dataAdmin.child("admin_account").addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var adminValue = snapshot.getValue().toString()
                        user?.let {
                            authNow = user.email.toString()
                            if (adminValue.compareTo(authNow) == 0) {
                                val intent = Intent(
                                    this@MainScreenActivity,
                                    ManageArticleActivity::class.java
                                )
                                startActivity(intent)
                            } else {
                                Toast.makeText(baseContext, "This feature is for admin only",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            R.id.ic_search -> {
                val intent = Intent(this@MainScreenActivity, SearchActivity::class.java)
                startActivity(intent)
            }

        }
        true

    }
}