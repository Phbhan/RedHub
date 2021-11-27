package com.redhub.repository

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.redhub.view.LoginActivity


class RegistrationActivityRepository(val application: Application) {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //private var mDatabase = FirebaseDatabase.getInstance().getReference("Users")
    lateinit var mDatabase : DatabaseReference
    val isSuccessful = MutableLiveData<Boolean>()


    fun createUser(mail: String,password: String,username:String) {
        //call fireBase service
        firebaseAuth.createUserWithEmailAndPassword(mail,password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    isSuccessful.value = it.isSuccessful

                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                        photoUri = Uri.parse("gs://redhub-a0b58.appspot.com/avatar/default_avatar.png")
                    }
                    // Add username to Auth
                    firebaseAuth.currentUser!!.updateProfile(profileUpdates)

                }
                else {
                    isSuccessful.value = false
                }
            }

    }




}