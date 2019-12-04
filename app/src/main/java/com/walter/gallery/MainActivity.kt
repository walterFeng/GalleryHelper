package com.walter.gallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        GalleryHelper.from(recyclerView)
            .attach(500, 40, 0.87f, 0.9f)
            .pagingScrollHelper
            .setOnPageChangeListener(object : PagingScrollHelper.OnPageChangeListener {
                override fun onPageChange(index: Int) {
                    Log.d("walter", "onPageChange:$index")
                }
            })
    }
}
