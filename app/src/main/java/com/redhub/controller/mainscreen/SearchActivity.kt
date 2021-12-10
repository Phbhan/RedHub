package com.redhub.controller.mainscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.RecyclerView
//import com.firebase.ui.database.FirebaseRecyclerAdapter
//import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.redhub.databinding.ActivitySearchBinding
//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.layout_list.view.*

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var database: DatabaseReference
    //lateinit var FirebaseRecyclerAdapter : FirebaseRecyclerAdapter<ArticleModel, ArticleViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Article")


        binding.searchText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val searchText = binding.searchText.getText().toString().trim()

                loadFirebaseData(searchText)
            }
        })

    }
    private fun loadFirebaseData(searchText : String) {

        if(searchText.isEmpty()){

//            binding.listView.adapter = FirebaseRecyclerAdapter
//
//        }else {
//
//
//                }
//
//            binding.listView.adapter = FirebaseRecyclerAdapter

        }

// // View Holder Class

        class ArticleViewHolder(var mview : View) : RecyclerView.ViewHolder(mview) {

       }

}



