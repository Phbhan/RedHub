package com.redhub.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.redhub.R
import com.redhub.databinding.ActivityLoginBinding
import com.redhub.viewmodel.LoginActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init viewmodel here
        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        binding.btnLogin.setOnClickListener {
            Log.d("login", "Login here")
            if (binding.edtEmail.text.isNullOrEmpty() || binding.edtPassword.text.isNullOrEmpty()) {

                Toast.makeText(application, "Vui lòng nhập đúng!", Toast.LENGTH_LONG).show()
            } else {
                //REQUEST login firebase here
                viewModel.requestLogin(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
            }
        }
        binding.btnRegister.setOnClickListener{
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        //khi dang nhap thanh cong hoac that bai
        viewModel.isSuccessful.observe(this, Observer {
            //handle
            var message = ""
            if (it == true) {
                message = "Login Success!"
            } else {
                message = "Login Failed!"
            }

            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        })

    }
}