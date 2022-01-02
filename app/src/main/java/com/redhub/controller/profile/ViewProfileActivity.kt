package com.redhub.controller.profile

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.R
import com.redhub.controller.article.ManageArticleActivity
import com.redhub.controller.mainscreen.MainScreenActivity
import com.redhub.controller.mainscreen.SearchActivity
import com.redhub.databinding.ActivityViewProfileBinding
import com.redhub.model.BriefArticleModel

class ViewProfileActivity : AppCompatActivity() {
    private val user = Firebase.auth.currentUser
    private val storage = FirebaseStorage.getInstance()
    var dataAdmin: DatabaseReference
    init{
        dataAdmin = FirebaseDatabase.getInstance().reference
    }
    private lateinit var myRef: DatabaseReference
    private lateinit var userListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
        myRef = database.getReference("user").child(user?.uid.toString())

        userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.tvUserDOB.text = dataSnapshot.child("DOB").value.toString()
                binding.tvUserFullname.text = dataSnapshot.child("fullname").value.toString()
                binding.tvUserGender.text = dataSnapshot.child("gender").value.toString()
                binding.tvUserPhone.text = dataSnapshot.child("phonenum").value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addListenerForSingleValueEvent(userListener)


        user?.let {
            // Name, email address, and profile photo
            binding.tvUsername.text = user.displayName
            binding.tvEmail.text = user.email

            val avatarUrl = user.photoUrl.toString()
            Log.d("Han", avatarUrl)
            val avatar = storage.getReferenceFromUrl(avatarUrl)
            avatar.downloadUrl.addOnSuccessListener { uri ->
                //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
                Glide.with(this)
                    .load(uri.toString())
                    .into(binding.ivAvatar)
            }.addOnFailureListener {
                // Handle any errors
            }
            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigation.setOnNavigationItemSelectedListener(navigasjonen)
        }

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
    private val navigasjonen = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.ic_home -> {
                val intent = Intent(this@ViewProfileActivity, MainScreenActivity::class.java)
                startActivity(intent)
            }

            R.id.ic_profile -> {
                return@OnNavigationItemSelectedListener false
            }
            R.id.ic_save -> {
                var adminValue: String
                var authNow: String

                dataAdmin.child("admin_account").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var adminValue = snapshot.getValue().toString()
                        user?.let {
                            authNow = user.email.toString()
                            if (adminValue.compareTo(authNow) == 0) {
                                val intent = Intent(
                                    this@ViewProfileActivity,
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
                val intent = Intent(this@ViewProfileActivity, SearchActivity::class.java)
                startActivity(intent)
            }

        }
        true

    }
}