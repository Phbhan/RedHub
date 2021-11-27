package com.redhub.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.redhub.R
import com.redhub.databinding.ActivityLoginBinding
import com.redhub.databinding.ActivityRegistrationBinding
import com.redhub.viewmodel.RegistrationActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btnRegister
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistrationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_registration)
        viewModel = ViewModelProvider(this).get(RegistrationActivityViewModel::class.java)
        val binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        binding.btnRegister.setOnClickListener {

            if (binding.edtEmail.text.isNullOrEmpty() || binding.edtPassword.text.isNullOrEmpty()||binding.edtUsername.text.isNullOrEmpty()) {

                Toast.makeText(application, "Vui lòng nhập đúng!", Toast.LENGTH_LONG).show()
            } else {
                //REQUEST login firebase here
                viewModel.createUser(binding.edtEmail.text.toString(), binding.edtPassword.text.toString(),binding.edtUsername.text.toString())
            }
        }

        //khi dang ky thanh cong hoac that bai
        viewModel.isSuccessful.observe(this, Observer {
            //handle
            var message = ""
            if (it == true) {
                message = "Sign up Success!"
            } else {
                message = "Sign up Failer!"
            }

            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        })
    }
}


