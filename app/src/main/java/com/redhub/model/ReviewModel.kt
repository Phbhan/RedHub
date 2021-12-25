package com.redhub.model

data class ReviewModel (
    val reviewId:String="",
    val username: String="",
    val review_content:String="",
    val like: Int=0
)