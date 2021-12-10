package com.redhub.model

import android.net.Uri
import java.util.*

data class UserModel (
    val userID: String="",
    val username: String = "",
    val password: String="",
    val DOB: String="",
    val imgUri: Uri
    )