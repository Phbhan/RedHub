package com.redhub

//import android.net.Uri
//import java.util.*

class ArticleModel {
    var title: String? = null
    var description: String? = null
    var release_date: String? = null
    var gerne: String? = null
    var director: List<String>? = null
    var star: List<Star>? = null
    var num_rate: Int? = null
    var posterUri: String? = null
    var rate: Int? = null
    var review: List<ReviewModel>? = null
    var youtubeID: String? = null

    constructor() {

    }
    constructor(title:String?,
                description:String?,
                release_date:String?,
                gerne:String?,
                director:List<String>?,
                star:List<Star>,
                num_rate: Int?,
                posterUri: String?,
                rate: Int?,
                review: List<ReviewModel>?,
                youtubeID: String?
    ){
        this.title= title
        this.description = description
        this.release_date=release_date
        this.gerne=gerne
        this.director=director
        this.star=star
        this.num_rate=num_rate
        this.posterUri=posterUri
        this.rate=rate
        this.review=review
        this.youtubeID=youtubeID

    }
}


class Star{
    var imgUri: String?=null
    var name: String?=null
    var role:String?=null

    constructor(){

    }
    constructor(imgUri: String,
                name: String?,
                role:String?){
        this.imgUri=imgUri
        this.name=name
        this.role=role
    }
}