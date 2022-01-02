package com.redhub.controller.article

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.redhub.R
import com.redhub.databinding.ActivityPostArticleBinding
import com.redhub.model.ArticleModel
import com.redhub.model.DirectorModel
import com.redhub.model.StarModel
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class PostArticleActivity : AppCompatActivity() {
    private var genres = arrayOf("Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy", "Historical", "Horror", "Mystery", "Romance", "Science fiction", "Thriller")
    private val selectedGenres = BooleanArray(genres.size)

    private val pickImage_star = 0
    private val pickImage_poster = 1

    lateinit var iv_star: ImageView
    lateinit var iv_poster: ImageView
    private var imageUri: Uri? = null
    private val starUriList = ArrayList<String>()
    private var idx_starUri by Delegates.notNull<Int>()
    lateinit var posterUri: String

    lateinit var ytID: String

    private val storage = FirebaseStorage.getInstance()
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostArticleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Genre
        binding.tvGenre.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Genre")
            builder.setMultiChoiceItems(genres, selectedGenres){dialog, index, isChecked ->
                selectedGenres[index] = isChecked
            }

            builder.setPositiveButton("OK"){ _, _ ->
                binding.tvGenre.text = ""
                for (i in selectedGenres.indices){
                    if (selectedGenres[i]) {
                        val genre = genres.get(i)
                        if (binding.tvGenre.text == "")
                            binding.tvGenre.text = genre
                        else
                            binding.tvGenre.text = binding.tvGenre.text.toString() + "; " + genre
                    }
                }
            }
            builder.setNeutralButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        // Director
        binding.btnAddDirector.setOnClickListener {
            val inflater = LayoutInflater.from(this).inflate(R.layout.director_row, null)
            binding.parentLinearLayoutDirector.addView(inflater, binding.parentLinearLayoutDirector.childCount)
            val btnDelete = inflater.findViewById<Button>(R.id.btn_delete_director)
            btnDelete.setOnClickListener {
                binding.parentLinearLayoutDirector.removeView(inflater)
            }
        }

        // Star
        binding.btnAddStar.setOnClickListener {
            val inflater = LayoutInflater.from(this).inflate(R.layout.star_row, null)
            binding.parentLinearLayoutStar.addView(inflater, binding.parentLinearLayoutStar.childCount)
            val btnDelete = inflater.findViewById<Button>(R.id.btn_delete_star)
            btnDelete.setOnClickListener {
                val index = binding.parentLinearLayoutStar.indexOfChild(inflater)
                if(starUriList.size > index) starUriList.removeAt(index)
                binding.parentLinearLayoutStar.removeView(inflater)
            }

            val iv_star_i = inflater.findViewById<ImageView>(R.id.iv_star)
            Glide.with(this)
                .load(R.drawable.default_avatar)
                .into(iv_star_i)
            iv_star_i.setOnClickListener {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                idx_starUri  = binding.parentLinearLayoutStar.indexOfChild(inflater)
                iv_star = iv_star_i
                startActivityForResult(gallery, pickImage_star)
            }
        }

        //Poster
        iv_poster = binding.ivPoster
        Glide.with(this)
            .load(R.drawable.default_poster)
            .override(1008 , 792)
            .into(iv_poster)
        binding.btnUploadPoster.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage_poster)
        }

        //Youtube video
        binding.btnAddYtid.setOnClickListener {
            ytID = binding.etYtId.text.toString()
            lifecycle.addObserver(binding.ytplayer)
            binding.ytplayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(ytID, 0F)
                }
            })
        }

        // Submit
        binding.btnSubmit.setOnClickListener {
            var check_empty: Boolean
            var check_director: Boolean
            val title = binding.etTitle.text.toString().toUpperCase()
            val releasedDate = binding.etReleaseDate.text.toString()
            val genres = binding.tvGenre.text.toString()
            val description = binding.etDescrip.text.toString()
            val youtubeID = binding.etYtId.text.toString()
            check_empty = (!checkEmpty(title, "Title") && !checkEmpty(releasedDate, "Released date")
                    && !checkEmpty(description, "Description") && !checkEmpty(youtubeID, "Youtube ID")
                    && !checkEmpty(genres, "Genre") && !checkEmpty(posterUri, "Poster"))


            val directors = getDirector(binding)
            check_director = (!(directors.size == 0))
            if (check_empty && check_director)
            {
                val stars = getStar(binding)
                Log.d("Han", posterUri)
                posterUri = uploadImageToFirebase(Uri.parse(posterUri), "poster")

                //val article = ArticleModel(title, releasedDate, genres, description, youtubeID, directors, stars,posterUri, 0, 0)

//                val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
//                val myRef = database.getReference("article")

                database = Firebase.database.reference
                val myRef = database.child("article")
                val articleId: String? = myRef.push().key
                if (articleId != null) {
                    myRef.child(articleId).child("articleId").setValue(articleId)
                    myRef.child(articleId).child("rate").setValue(0)
                    myRef.child(articleId).child("numrate").setValue(0)
                    for(i in 0 until stars.size)
                    {
                        myRef.child(articleId).child("star").child("$i").child("name").setValue(stars[i].name)
                        myRef.child(articleId).child("star").child("$i").child("role").setValue(stars[i].role)
                        myRef.child(articleId).child("star").child("$i").child("imgUri").setValue(stars[i].imgUri)
                    }
                    for(i in 0 until directors.size) {
                        myRef.child(articleId).child("director").child("$i").setValue(directors[i].name)
                    }
                    myRef.child(articleId).child("youtubeID").setValue(youtubeID)
                    myRef.child(articleId).child("description").setValue(description)
                    myRef.child(articleId).child("posterUri").setValue(posterUri)
                    myRef.child(articleId).child("genre").setValue(genres)
                    myRef.child(articleId).child("releasedDate").setValue(releasedDate)
                    myRef.child(articleId).child("title").setValue(title)
                }
                Toast.makeText(baseContext, "Posting success.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,ManageArticleActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnBack.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you want to back to Manage article? Your data is not saved!!!")
            builder.setPositiveButton("OK"){ _, _ ->
                val intent = Intent(this,ManageArticleActivity::class.java)
                startActivity(intent)
            }
            builder.setNeutralButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            imageUri = data?.data
            if(requestCode == pickImage_star) {
                iv_star.setImageURI(imageUri)
                Glide.with(this)
                    .load(imageUri)
                    .into(iv_star)
                //starUriList.add(imageUri.toString())
                if (idx_starUri == starUriList.size) {
                    starUriList.add(imageUri.toString())
                }else
                {
                    starUriList[idx_starUri] = imageUri.toString()
                }
            }
            if(requestCode == pickImage_poster) {
                iv_poster.setImageURI(imageUri)
                Glide.with(this)
                    .load(imageUri)
                    .override(1008, 792)
                    .into(iv_poster)
                posterUri = imageUri.toString()
            }
        }
    }
    private fun uploadImageToFirebase(imageUri: Uri?, role: String): String {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        var path = "gs://redhub-a0b58.appspot.com"
        var refStorage = storage.reference
        if(role == "star"){
            refStorage = refStorage.child("/Star/$fileName")
            var uploadTask  = refStorage.putFile(imageUri!!)

            uploadTask.addOnFailureListener {
                uploadTask.resume()
                Log.d("Han", "Upload image fail")
            }.addOnSuccessListener { _ ->
                Log.d("Han", "Upload image success")
            }
            path = "$path/Star/$fileName"
        }
        if(role == "poster"){
            refStorage = refStorage.child("/Poster/$fileName")
            var uploadTask  = refStorage.putFile(imageUri!!)
            uploadTask.addOnFailureListener {
                uploadTask.resume()
                Log.d("Han", "Upload image fail")
            }.addOnSuccessListener { _ ->
                Log.d("Han", "Upload image success")
            }
            path = "$path/Poster/$fileName"
        }
        return path
    }
    private fun getDirector(binding: ActivityPostArticleBinding): ArrayList<DirectorModel> {
        val directorList = ArrayList<DirectorModel>()
        val count = binding.parentLinearLayoutDirector.childCount
        var v: View?
        for (i in 0 until count) {
            v = binding.parentLinearLayoutDirector.getChildAt(i)

            val directorName: EditText = v.findViewById(R.id.et_name_director)
            directorList.add(DirectorModel(directorName.text.toString()))
        }
        return directorList
    }
    private fun getStar(binding: ActivityPostArticleBinding): ArrayList<StarModel> {
        val starList = ArrayList<StarModel>()
        val count = binding.parentLinearLayoutStar.childCount
        var v: View?

        for (i in 0 until count) {
            v = binding.parentLinearLayoutStar.getChildAt(i)

            val starName: EditText = v.findViewById(R.id.et_name_star)
            val starRole: EditText = v.findViewById(R.id.et_role_star)
            var imageUri = uploadImageToFirebase(Uri.parse(starUriList[i]), "star")

            starList.add(StarModel(starName.text.toString(), starRole.text.toString(), imageUri))
        }
        return starList
    }
    private fun checkEmpty(et: String, name: String): Boolean
    {
        if (et.isEmpty())
        {
            Toast.makeText(baseContext, "$name is empty!",
                Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }
}