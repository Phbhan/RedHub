package com.redhub.controller.mainscreen

//import com.firebase.ui.database.FirebaseRecyclerAdapter
//import com.firebase.ui.database.FirebaseRecyclerOptions
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.R
import com.redhub.controller.article.ManageArticleActivity
import com.redhub.controller.profile.ViewProfileActivity
import com.redhub.databinding.ActivitySearchBinding
import com.redhub.model.ArticleModel
import com.redhub.model.BriefArticleModel
import com.redhub.model.ReviewModel


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var database: DatabaseReference
    //private lateinit var searchRecyclerView: RecyclerView
    //private lateinit var list_search : ArrayList<ArticleModel>
    //private var searchAdapter:SearchAdapter?=null
    private var searchEditText: TextInputEditText?=null
    private  lateinit var firebaseSearchQuery : Query

    private lateinit var searchListener: ValueEventListener
    private lateinit var myRef: DatabaseReference
    private val storage = FirebaseStorage.getInstance()
    private val user = Firebase.auth.currentUser

    var dataAdmin: DatabaseReference
    init{
        dataAdmin = FirebaseDatabase.getInstance().reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //searchRecyclerView = binding.listView
        //searchRecyclerView.layoutManager=LinearLayoutManager(this)
        //searchRecyclerView.setHasFixedSize(true)
//
        //list_search = arrayListOf<ArticleModel>()

        searchEditText = binding.searchText

        val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
        myRef = database.getReference("article")

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                loadFirebaseData(myRef,p0.toString().toUpperCase())
            }
        })



        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigasjonen)
    }

    private fun loadFirebaseData(myRef: DatabaseReference,searchText: String) {
        val firebaseSearchQuery = myRef.orderByChild("title")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")
        addSearchEventListener(firebaseSearchQuery )


    }
    private fun addSearchRow(article: BriefArticleModel, articleID: String)
    {
        val inflater = LayoutInflater.from(this).inflate(R.layout.layout_search_list, null)
        binding.parentLinearLayoutSearch.addView(inflater, binding.parentLinearLayoutSearch.childCount)
        val tv_title= inflater.findViewById<TextView>(R.id.movie_title)
        tv_title.text = article.title
        val tv_rate= inflater.findViewById<TextView>(R.id.movie_rate)
        tv_rate.text = article.rates.toString()
        val iv_poster = inflater.findViewById<ImageView>(R.id.ArticleImageView)

        val poster = storage.getReferenceFromUrl(article.posterUri)
        poster.downloadUrl.addOnSuccessListener { uri ->
            //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
            Glide.with(this)
                .load(uri.toString())
                .override(378 , 297)
                .into(iv_poster)
        }.addOnFailureListener {
            // Handle any errors
        }
        inflater.setOnClickListener {
            val intent = Intent(this, ReadArticleActivity::class.java)
            intent.putExtra("articleId", articleID)
            //firebaseSearchQuery.removeEventListener(searchListener)
            startActivity(intent)
        }

    }
    private fun addSearchEventListener(firebaseSearchQuery : Query) {
        // [START post_value_event_listener]
        searchListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutSearch.removeAllViews()
                for (articleSnapshot in dataSnapshot.getChildren()) {
                    val title = articleSnapshot.child("title").value.toString()
                    val posterUri = articleSnapshot.child("posterUri").value.toString()
                    val rate = articleSnapshot.child("rate").value.toString().toFloat()
                    val briefarticle = BriefArticleModel(title, posterUri, rate)
                    val articleId = articleSnapshot.key
                    if (articleId != null) {
                        addSearchRow(briefarticle, articleId)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        firebaseSearchQuery .addValueEventListener(searchListener)
        // [END post_value_event_listener]
    }




    private val navigasjonen = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.ic_home -> {
                val intent = Intent(this@SearchActivity, MainScreenActivity::class.java)
                startActivity(intent)
            }

            R.id.ic_profile -> {
                val intent = Intent(this@SearchActivity, ViewProfileActivity::class.java)
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
                                    this@SearchActivity,
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
                return@OnNavigationItemSelectedListener false
            }

        }
        true

    }
}





