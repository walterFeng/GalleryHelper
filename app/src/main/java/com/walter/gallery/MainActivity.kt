package com.walter.gallery

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by walter on 2019/12/4.
 * Email: fengxiao1493@qq.com
 * A demo support gallery using RecyclerView
 * 使recyclerView支持画廊效果的demo
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init recyclerView:
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = setupAdapter(recyclerView)

        // support gallery:
        val helper = GalleryHelper.from(recyclerView)
            .attach(loopParams = 500, itemSpace = 40, scale = 0.87f, alpha = 0.9f)

        // add page changed listener
        helper.pagingScrollHelper
            .setOnPageChangedListener(object : PagingScrollHelper.OnPageChangedListener {
                override fun onPageChange(index: Int) {
                    Log.d("walter", "onPageChange:$index")
                    val i = helper.lopperHelper.gerCurrentRealPosition()
                    val position = i % adapter.itemCount
                    Log.d("walter", "position=$position")
                }
            })
    }

    private fun setupAdapter(recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, //has support LinearLayoutManager.VERTICAL 支持垂直滑动
            false
        )
        recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
                return ViewHolder(TextView(this@MainActivity))
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
                viewHolder.onBind(i)
            }

            override fun getItemCount(): Int {
                return 3
            }
        }
        return recyclerView.adapter!!
    }


    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var textView: TextView = itemView as TextView

        init {
            textView.textSize = 28f
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textView.layoutParams = params
            textView.gravity = Gravity.CENTER
            textView.setBackgroundColor(Color.RED)
        }

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            textView.text = if (position == 0) "I am \n the first image" else "image $position"
        }
    }
}
