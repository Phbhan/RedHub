package com.redhub.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.redhub.R
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
        setContentView(R.layout.activity_registration)

        //viewModel = ViewModelProvider(this).get(RegistrationActivityViewModel::class.java)
        viewModel = ViewModelProvider(this).get(RegistrationActivityViewModel::class.java)

        btnRegister.setOnClickListener {

            if (isValidData()) {
                //REQUEST login firebase here
                viewModel.createUser(edtEmail.text.toString(), edtPassword.text.toString(),edtUsername.text.toString())
            } else {
                Toast.makeText(application, "Vui lòng nhập đúng!", Toast.LENGTH_LONG).show()
            }
        }

        //khi dang ky thanh cong hoac that bai
        viewModel.isSuccessful.observe(this, Observer {
            //handle
            var message = ""
            if (it == true) {
                message = "Đăng ký thành công!"
            } else {
                message = "Đăng ký thất bại!"
            }

            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        })

        //check valid data
        //true valid - failed invalid

    }

    private fun isValidData(): Boolean {
        if (edtEmail.text.isNullOrEmpty() || edtPassword.text.isNullOrEmpty()||edtUsername.text.isNullOrEmpty()) {
            return false
        }
        return true
    }
}


