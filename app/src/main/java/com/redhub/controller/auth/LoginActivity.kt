package com.redhub.controller.auth

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.redhub.controller.mainscreen.MainScreenActivity
import com.redhub.databinding.ActivityLoginBinding
import com.redhub.controller.profile.ViewProfileActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener{
            var email = binding.edtEmail.text.toString().trim()
            var password = binding.edtPassword.text.toString().trim()

            if (email.isEmpty())
                Toast.makeText(this,"Input email", Toast.LENGTH_LONG).show()
            else
                if (password.isEmpty())
                    Toast.makeText(this,"Input password", Toast.LENGTH_LONG).show()
                else
                {
                 Toast.makeText(this,"Login success",Toast.LENGTH_LONG).show()
                    requestLogin(email,password)
                }
        }
        binding.btnRegister.setOnClickListener{
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
    private fun requestLogin(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainScreenActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END sign_in_with_email]
    }
}