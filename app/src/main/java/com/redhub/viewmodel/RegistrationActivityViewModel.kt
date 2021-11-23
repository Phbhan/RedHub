package com.redhub.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.redhub.model.RegisterModel
import com.redhub.repository.RegistrationActivityRepository

class RegistrationActivityViewModel(application: Application) : AndroidViewModel(application){
    private val repository  = RegistrationActivityViewModel(application)
    val isSuccessful : LiveData<Boolean>

    init {
        isSuccessful = repository.isSuccessful
    }

    //create user in filebase
    fun createUser(mail: String,password: String,username:String) {
        repository.createUser(mail, password,username)
    }

}