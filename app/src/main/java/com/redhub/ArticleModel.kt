package com.redhub

import android.net.Uri
import java.util.*

data class ArticleModel (
    val title : String="",
    val description : String="",
    val release_date : String="",
    val gerne: String="",
    val director: List<String>,
    val star: List<Star>,
    val numrate: Int=0,
    val posterUri: Uri,
    val rate: Short=0,
    val review: ReviewModel,
    val youtubeID: String=""
)
data class Star(
    val imgUri: Uri,
    val name: String="",
    val role:String=""
)