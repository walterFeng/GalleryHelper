package com.walter.gallery

import android.support.v7.widget.RecyclerView

class GalleryHelper {

    lateinit var recyclerView: RecyclerView

    val pagingScrollHelper: PagingScrollHelper = PagingScrollHelper()
    val scaleTransformHelper: ScaleTransformHelper = ScaleTransformHelper()
    val lopperHelper: LopperHelper = LopperHelper()

    @JvmOverloads
    fun attach(
        loopParams: Int = 0,
        itemSpace: Int = 0,
        scale: Float = 0f,
        alpha: Float = 0f
    ): GalleryHelper {
        pagingScrollHelper.attachToRecyclerView(recyclerView, recyclerView.adapter!!)
        scaleTransformHelper.attachToRecyclerView(recyclerView, scale, alpha)
        lopperHelper.attachToRecyclerView(recyclerView, loopParams > 0, loopParams, itemSpace)
        return this
    }

    companion object {

        @JvmStatic
        fun from(recyclerView: RecyclerView): GalleryHelper {
            val galleryHelper = GalleryHelper()
            galleryHelper.recyclerView = recyclerView
            return galleryHelper
        }
    }
}
