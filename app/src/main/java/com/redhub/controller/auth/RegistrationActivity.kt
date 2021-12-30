package com.redhub.controller.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.redhub.controller.profile.EditProfileActivity
import com.redhub.model.UserModel
import com.redhub.databinding.ActivityRegistrationBinding
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val username = binding.edtUsername.text.toString().trim()

            if(username.isEmpty()){
                binding.edtUsername.setError("User name is required!")
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            }
            if(email.isEmpty()){
                binding.edtEmail.setError("Email is required!")
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if(PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmail.setError("Please provide valid email!")
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.edtPassword.setError("Password is required!")
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }
            if(password.length <6){
                binding.edtPassword.setError("Min password length should be 6 characters!")
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            // ...
            // Initialize Firebase Auth
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    //signup success
                    //get current user
                    val firebaseUser = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder().apply {
                        displayName = username
                        photoUri = Uri.parse("gs://redhub-a0b58.appspot.com/avatar/default_avatar.png")
                    }.build()
                    Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
            }
                .addOnFailureListener {e->
                    if(e is FirebaseAuthUserCollisionException)
                        Toast.makeText(this,"Account created with email $email",Toast.LENGTH_SHORT).show()
                    Toast.makeText(this,"SignUp Failed due to ${e.message}",Toast.LENGTH_SHORT).show()
                }

        }

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}


