package com.redhub.controller.mainscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
//import com.firebase.ui.database.FirebaseRecyclerAdapter
//import com.firebase.ui.database.FirebaseRecyclerOptions
import com.redhub.databinding.ActivitySearchBinding
import com.redhub.model.ArticleModel
import com.redhub.model.DirectorModel

//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.layout_list.view.*

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Article")


        binding.searchText.addTextChangedListener(object : TextWatcher {
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

    private fun loadFirebaseData(searchText: String) {

        val firebaseSearchQuery = database.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff")
        firebaseSearchQuery.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list_search = ArrayList<ArticleModel>()
                for (search in snapshot.child("article").children)
                {
                    val model = search.getValue(ArticleModel::class.java)
                    list_search.add(model as ArticleModel)
                }
                if(list_search.size >0)
                {
                    val adapter = SearchAdapter(list_search)
                    binding.listView.adapter = adapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel",error.toString())
            }

        })

    }
}





