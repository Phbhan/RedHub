package com.redhub

data class ReviewModel (
    val user: UserModel,
    val review_content:String="",
    val like: Int=0,
    val dislike:Int=0
)