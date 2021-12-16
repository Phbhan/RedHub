package com.redhub.controller.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.redhub.model.UserModel
import com.redhub.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val username = binding.edtUsername.text.toString()

            // ...
            // Initialize Firebase Auth

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //val user = auth.currentUser
                        //val profileUpdates = userProfileChangeRequest {
                        //    displayName = username
                        //    photoUri = Uri.parse("gs://redhub-a0b58.appspot.com/avatar/default_avatar.png")
                        //}
                        // Add username to Auth
                       // auth.currentUser!!.updateProfile(profileUpdates)
                        val photoUri = Uri.parse("gs://redhub-a0b58.appspot.com/avatar/default_avatar.png")
                        val database = Firebase.database
                        val myRef = database.getReference("user")
                        val userId = myRef.push().key

                        val user = UserModel(userID = userId.toString(),username = username,password,"",photoUri)

                        myRef.child(userId.toString()).setValue(user)

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)


                    } else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}