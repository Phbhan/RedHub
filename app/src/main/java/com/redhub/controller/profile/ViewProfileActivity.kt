package com.redhub.controller.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.databinding.ActivityViewProfileBinding

class ViewProfileActivity : AppCompatActivity() {
    private val user = Firebase.auth.currentUser
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        user?.let {
            // Name, email address, and profile photo
            binding.tvUsername.text = user.displayName
            binding.tvEmail.text = user.email

            val avatarUrl = user.photoUrl.toString()
            val avatar = storage.getReferenceFromUrl(avatarUrl)
            avatar.downloadUrl.addOnSuccessListener { uri ->
                //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
                Glide.with(this)
                    .load(uri.toString())
                    .into(binding.ivAvatar)
            }.addOnFailureListener {
                // Handle any errors
            }
        }

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}