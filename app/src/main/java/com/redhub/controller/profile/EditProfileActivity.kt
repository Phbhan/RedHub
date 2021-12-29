package com.redhub.controller.profile

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.databinding.ActivityEditProfileBinding
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private val user = Firebase.auth.currentUser
    private val storage = FirebaseStorage.getInstance()

    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
    private lateinit var userListener: ValueEventListener
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        imageView = binding.ivAvatar
        binding.etEmail.setText(user?.email)
        val avatarUrl = user?.photoUrl.toString()
        val pic = storage.getReferenceFromUrl(avatarUrl)
        pic.downloadUrl.addOnSuccessListener { uri ->
            //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
            Glide.with(this)
                .load(uri.toString())
                .into(imageView)
        }.addOnFailureListener {
            // Handle any errors
        }
        val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
        myRef = database.getReference("user").child(user?.uid.toString())

        userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.etDOB.setText(dataSnapshot.child("DOB").value.toString())
                binding.etFullname.setText(dataSnapshot.child("fullname").value.toString())
                binding.etGender.setText(dataSnapshot.child("gender").value.toString())
                binding.etPhone.setText(dataSnapshot.child("phonenum").value.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addListenerForSingleValueEvent(userListener)


        binding.btnEdPassword.setOnClickListener {
            val newPassword = binding.etPassword.text.toString()
            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "User password updated.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        binding.btnUploadAvatar.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        binding.btnSubmit.setOnClickListener {
            val database = Firebase.database.reference
            val myRef = database.child("user")
            val userId: String? = user?.uid
            if (userId != null) {
                myRef.child(userId).child("DOB").setValue(binding.etDOB.text.toString())
                myRef.child(userId).child("fullname").setValue(binding.etFullname.text.toString())
                myRef.child(userId).child("gender").setValue(binding.etGender.text.toString())
                myRef.child(userId).child("phonenum").setValue(binding.etPhone.text.toString())
            }
            val newEmail = binding.etEmail.text.toString()
            user!!.updateEmail(newEmail)

            val intent = Intent(this, ViewProfileActivity::class.java)
            startActivity(intent)

        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ViewProfileActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
            uploadImageToFirebase(imageUri)
        }
    }
    private fun uploadImageToFirebase(imageUri: Uri?) {
        if (imageUri == null) return

        val fileName = UUID.randomUUID().toString() + ".jpg"

        val refStorage = storage.reference.child("/avatar/$fileName")

        refStorage.putFile(imageUri!!)
            .addOnSuccessListener {
                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse("gs://redhub-a0b58.appspot.com" + "/avatar/$fileName")
                }
                user!!.updateProfile(profileUpdates)
                Toast.makeText(baseContext, "Image uploaded",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Image upload fail",
                    Toast.LENGTH_SHORT).show()
            }
    }
}