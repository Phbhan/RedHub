package com.redhub.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
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

        binding.btnEdEmail.setOnClickListener{
            val newEmail = binding.etEmail.text.toString()
            user!!.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "User email updated.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

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

        val refStorage = storage.reference.child("/Avatar/$fileName")

        refStorage.putFile(imageUri!!)
            .addOnSuccessListener {
                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse("gs://test-c9095.appspot.com" + "/Avatar/$fileName")
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