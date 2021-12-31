package com.redhub.controller.mainscreen

//import com.firebase.ui.database.FirebaseRecyclerAdapter
//import com.firebase.ui.database.FirebaseRecyclerOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.redhub.R
import com.redhub.controller.article.ManageArticleActivity
import com.redhub.controller.profile.ViewProfileActivity
import com.redhub.databinding.ActivitySearchBinding
import com.redhub.model.ArticleModel


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var database: DatabaseReference
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var list_search : ArrayList<ArticleModel>
    private var searchAdapter:SearchAdapter?=null
    private var searchEditText: TextInputEditText?=null

    private val user = Firebase.auth.currentUser

    var dataAdmin: DatabaseReference
    init{
        dataAdmin = FirebaseDatabase.getInstance().reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchRecyclerView = binding.listView
        searchRecyclerView.layoutManager=LinearLayoutManager(this)
        searchRecyclerView.setHasFixedSize(true)

        list_search = arrayListOf<ArticleModel>()

        searchEditText = binding.searchText

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                loadFirebaseData(p0.toString().toUpperCase())
            }
        })

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigasjonen)
    }

    private fun loadFirebaseData(searchText: String) {
        database = FirebaseDatabase.getInstance().reference
            .child("article")

        val firebaseSearchQuery = database.orderByChild("title")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")
        firebaseSearchQuery.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (search in snapshot.children)
                    {
                        val model = search.getValue(ArticleModel::class.java)
                        list_search.add(model!!)
                    }
                    searchAdapter = SearchAdapter(list_search)
                    searchAdapter!!.setOnItemClickListener(object:SearchAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val articleId = list_search[position].articleId

                            val intent: Intent = Intent(applicationContext,ReadArticleActivity::class.java)
                            intent.putExtra("articleId", articleId)
                            startActivity(intent)

                        }

                    })
                    searchRecyclerView.adapter = searchAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })

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





