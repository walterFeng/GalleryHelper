package com.walter.gallery

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by walter on 2019/12/4.
 * Email: fengxiao1493@qq.com
 * A demo support gallery using RecyclerView
 * 使recyclerView支持画廊效果的demo
 */
class MainActivity : AppCompatActivity() {

    private var orientation = LinearLayoutManager.HORIZONTAL

    private var helper: GalleryHelper? = null

    private val images = arrayListOf(
        R.drawable.wechat_img137,
        R.drawable.wechat_img138,
        R.drawable.wechat_img139,
        R.drawable.wechat_img140
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        // init recyclerView:
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = setupAdapter(recyclerView)

        // support gallery:
        helper = GalleryHelper.from(recyclerView)
            .attach(loopParams = 500, itemSpace = 40, scale = 0.87f, alpha = 0.9f)

        // add page changed listener:
        helper?.pagingScrollHelper?.setOnPageChangedListener(object :
            PagingScrollHelper.OnPageChangedListener {
            override fun onPageChange(index: Int) {
                val i = helper?.lopperHelper?.gerCurrentRealPosition() ?: 0
                val position = i % adapter.itemCount
                Log.d("walter", "onPageChange:$index || position=$position")
            }
        })
    }

    private fun setupAdapter(recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        recyclerView.layoutManager = LinearLayoutManager(this, orientation, false)
        recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
                return ViewHolder(ImageView(this@MainActivity))
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
                viewHolder.onBind(i)
            }

            override fun getItemCount(): Int = images.size
        }
        return recyclerView.adapter!!
    }

    @Suppress("all")
    fun changeOrientation(v: View) {
        orientation = if (orientation == LinearLayoutManager.HORIZONTAL)
            LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL
        helper?.detached()
        init()
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var imageView: ImageView = itemView as ImageView

        init {
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.layoutParams = params
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setBackgroundColor(Color.RED)
        }

        fun onBind(position: Int) {
            Glide.with(itemView.context).load(images[position]).into(imageView)
        }
    }
}
