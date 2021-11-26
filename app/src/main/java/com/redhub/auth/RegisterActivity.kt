package com.redhub.auth

//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.userProfileChangeRequest
//import com.redhub.databinding.ActivityRegisterBinding
//import com.redhub.view.LoginActivity
//
//class RegisterActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivityRegisterBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        auth = FirebaseAuth.getInstance()
//
//        binding.btnRegister.setOnClickListener{
//            val email = binding.etEmailSu.text.toString()
//            val password = binding.etPasswordSu.text.toString()
//            val username = binding.etUsername.text.toString()
//
//            // ...
//            // Initialize Firebase Auth
//
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        //val user = auth.currentUser
//                        val profileUpdates = userProfileChangeRequest {
//                            displayName = username
//                            photoUri = Uri.parse("gs://redhub-a0b58.appspot.com/avatar/default_avatar.png")
//                        }
//                        // Add username to Auth
//                        auth.currentUser!!.updateProfile(profileUpdates)
//                        val intent = Intent(this, LoginActivity::class.java)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(baseContext, "Authentication failed.",
//                            Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//        }
//    }
//}