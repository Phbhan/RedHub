package com.redhub.model

data class ReviewModel (
    val username: String="",
    val avatar: String="",
    val review_content:String="",
    val like: Int=0
)