package com.redhub.model

import com.redhub.model.DirectorModel
import com.redhub.model.StarModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ArticleModel(val title: String, val releasedDate: String,
                   val genre: String, val description: String,
                   val youtubeID: String, val directors: ArrayList<DirectorModel>,
                   val stars: ArrayList<StarModel>, val posterUri: String,
                   val rates: Float, val numRate: Int)
