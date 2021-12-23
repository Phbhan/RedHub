package com.redhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenu
import com.redhub.controller.article.ManageArticleActivity
import com.redhub.controller.auth.RegistrationActivity
import com.redhub.controller.mainscreen.MainScreenActivity
import com.redhub.controller.mainscreen.ReadArticleActivity
import com.redhub.controller.mainscreen.SearchActivity
import com.redhub.controller.profile.ViewProfileActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


}