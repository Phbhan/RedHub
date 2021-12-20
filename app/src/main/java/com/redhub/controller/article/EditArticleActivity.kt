package com.redhub.controller.article

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.redhub.R
import com.redhub.controller.mainscreen.DirectorAdapter
import com.redhub.controller.mainscreen.StarAdapter
import com.redhub.databinding.ActivityEditArticleBinding
import com.redhub.databinding.ActivityPostArticleBinding
import com.redhub.model.BriefArticleModel
import com.redhub.model.DirectorModel
import com.redhub.model.StarModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class EditArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditArticleBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference
    private lateinit var directorListener: ValueEventListener
    private lateinit var starListener: ValueEventListener

    private var genres = arrayOf("Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy", "Historical", "Horror", "Mystery", "Romance", "Science fiction", "Thriller")
    private val selectedGenres = BooleanArray(genres.size)

    lateinit var iv_poster: ImageView
    lateinit var iv_star: ImageView

    private val pickImage_star = 0
    private val pickImage_poster = 1

    private var imageUri: Uri? = null
    private val starUriList = ArrayList<String>()
    private var idx_starUri by Delegates.notNull<Int>()
    private var posterUri = ""

    private lateinit var articleId: String
//    private lateinit var title_org: String
//    private lateinit var description_org: String
//    private lateinit var released_date_org: String
//    private lateinit var genre_org: String
//    private lateinit var youtubeID_org: String
//    private lateinit var poster_org: String
//    private lateinit var lst_director_org: ArrayList<DirectorModel>
//    private lateinit var lst_star_org: ArrayList<StarModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            articleId = extras.getString("articleID").toString()
            Log.d("Han", articleId)
        }
        storage= FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance().getReference("article")
        readData(articleId)

        //Genre
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

        //Youtube
        binding.btnAddYtid.setOnClickListener {
            var ytID = binding.etYtId.text.toString()
            lifecycle.addObserver(binding.ytplayer)
            binding.ytplayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(ytID, 0F)
                }
            })
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
                starUriList.removeAt(index)
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

        // Submit
        binding.btnSubmit.setOnClickListener {
            var check_empty: Boolean
            var check_director: Boolean
            val title = binding.etTitle.text.toString()
            val releasedDate = binding.etReleaseDate.text.toString()
            val genres = binding.tvGenre.text.toString()
            val description = binding.etDescrip.text.toString()
            val youtubeID = binding.etYtId.text.toString()
            check_empty = (!checkEmpty(title, "Title") && !checkEmpty(releasedDate, "Released date")
                    && !checkEmpty(description, "Description") && !checkEmpty(youtubeID, "Youtube ID")
                    && !checkEmpty(genres, "Genre"))

            val directors = getDirector(binding)
            check_director = (!(directors.size == 0))
            if (check_empty && check_director)
            {
                val stars = getStar(binding)
                if (!posterUri.isEmpty())
                    posterUri = uploadImageToFirebase(Uri.parse(posterUri), "poster")

                database = Firebase.database.reference
                val myRef = database.child("article")
                if (articleId != null) {
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
                    if (!posterUri.isEmpty())
                        myRef.child(articleId).child("posterUri").setValue(posterUri)
                    myRef.child(articleId).child("genre").setValue(genres)
                    myRef.child(articleId).child("releasedDate").setValue(releasedDate)
                    myRef.child(articleId).child("title").setValue(title)
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Do you want to back to Manage article?")
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
    }

    private fun readData(articleId: String) {

        database.child(articleId).get().addOnSuccessListener {
            if (it.exists()) {
                val title = it.child("title").value.toString()
                val description = it.child("description").value.toString()
                val released_date = it.child("releasedDate").value.toString()
                val genre = it.child("genre").value.toString()
                val youtubeID = it.child("youtubeID").value.toString()
                val poster = it.child("posterUri").value.toString()

                binding.etTitle.setText(title)
                binding.etDescrip.setText(description)
                binding.etReleaseDate.setText(released_date)
                binding.tvGenre.setText(genre)
                binding.etYtId.setText(youtubeID)

                val getPoster = storage.getReferenceFromUrl(poster)
                getPoster.downloadUrl.addOnSuccessListener { uri ->
                    //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
                    Glide.with(this)
                        .load(uri.toString())
                        .override(1008 , 792)
                        .into(binding.ivPoster)
                }.addOnFailureListener {
                    // Handle any errors
                }

                addDirectorEventListener(database.child(articleId).child("director"))
                addStarEventListener(database.child(articleId).child("star"))
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDirectorEventListener(dbReference: DatabaseReference) {
        // [START post_value_event_listener]
        directorListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutDirector.removeAllViews()
                for (directorSnapshot in dataSnapshot.getChildren()) {
                    val name_val = directorSnapshot.getValue().toString()

                    val inflater = LayoutInflater.from(this@EditArticleActivity).inflate(R.layout.director_row, null)
                    binding.parentLinearLayoutDirector.addView(inflater, binding.parentLinearLayoutDirector.childCount)
                    val director_name = inflater.findViewById<EditText>(R.id.et_name_director)
                    director_name.setText(name_val)
                    val btnDelete = inflater.findViewById<Button>(R.id.btn_delete_director)
                    btnDelete.setOnClickListener {
                        binding.parentLinearLayoutDirector.removeView(inflater)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(directorListener)
        // [END post_value_event_listener]
    }

    private fun addStarEventListener(dbReference: DatabaseReference) {
        // [START post_value_event_listener]
        starListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutStar.removeAllViews()
                for (starSnapshot in dataSnapshot.getChildren()) {
                    val name_val = starSnapshot.child("name").getValue().toString()
                    val role_val = starSnapshot.child("role").getValue().toString()
                    val img_star = starSnapshot.child("imgUri").getValue().toString()
                    starUriList.add(img_star)

                    val inflater = LayoutInflater.from(this@EditArticleActivity).inflate(R.layout.star_row, null)
                    binding.parentLinearLayoutStar.addView(inflater, binding.parentLinearLayoutStar.childCount)
                    val star_name = inflater.findViewById<EditText>(R.id.et_name_star)
                    val star_role= inflater.findViewById<EditText>(R.id.et_role_star)
                    val iv_star_i = inflater.findViewById<ImageView>(R.id.iv_star)
                    val btnDelete = inflater.findViewById<Button>(R.id.btn_delete_star)

                    star_name.setText(name_val)
                    star_role.setText(role_val)
                    val starUri = storage.getReferenceFromUrl(img_star)
                    starUri.downloadUrl.addOnSuccessListener { uri ->
                        //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
                        Glide.with(this@EditArticleActivity)
                            .load(uri.toString())
                            .into(iv_star_i)
                    }.addOnFailureListener {
                        // Handle any errors
                    }

                    iv_star_i.setOnClickListener {
                        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        idx_starUri  = binding.parentLinearLayoutStar.indexOfChild(inflater)
                        iv_star = iv_star_i
                        startActivityForResult(gallery, pickImage_star)
                    }
                    btnDelete.setOnClickListener {
                        val index = binding.parentLinearLayoutStar.indexOfChild(inflater)
                        starUriList.removeAt(index)
                        binding.parentLinearLayoutStar.removeView(inflater)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(starListener)
        // [END post_value_event_listener]
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
    private fun getDirector(binding: ActivityEditArticleBinding): ArrayList<DirectorModel> {
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
    private fun getStar(binding: ActivityEditArticleBinding): ArrayList<StarModel> {
        val starList = ArrayList<StarModel>()
        val count = binding.parentLinearLayoutStar.childCount
        var v: View?

        for (i in 0 until count) {
            v = binding.parentLinearLayoutStar.getChildAt(i)

            val starName: EditText = v.findViewById(R.id.et_name_star)
            val starRole: EditText = v.findViewById(R.id.et_role_star)
            var imageUri = starUriList[i]
            if (starUriList[i].contains("gs://redhub-a0b58.appspot.com") == false) {
                imageUri = uploadImageToFirebase(Uri.parse(starUriList[i]), "star")
            }
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
