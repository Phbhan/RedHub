package com.redhub.model

data class ArticleModel(
    val articleId: String, val title: String, val releasedDate: String,
    val genre: String, val description: String,
    val youtubeID: String, val directors: Array<DirectorModel>,
    val stars: Array<StarModel>, val posterUri: String, val rates:Float){
    constructor():this("","","","","","", emptyArray<DirectorModel>(), emptyArray<StarModel>(),"",0f)
}

