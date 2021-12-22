package com.redhub.model

data class ReviewModel (
    val username: String="",
    val review_content:String="",
    val like: Int=0,
    val dislike:Int=0
)