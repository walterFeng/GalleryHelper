package com.walter.gallery

import android.support.v7.widget.RecyclerView

/**
 * Created by walter on 2019/12/4.
 * Email: fengxiao1493@qq.com
 * A gallery-enabled tool
 * 支持recyclerView画廊滑动的工具类
 */
class GalleryHelper {

    lateinit var recyclerView: RecyclerView

    val pagingScrollHelper: PagingScrollHelper = PagingScrollHelper()
    val scaleTransformHelper: ScaleTransformHelper = ScaleTransformHelper()
    val lopperHelper: LopperHelper = LopperHelper()

    /**
     *  @param loopParams adapterCount will be multiplied by this value to implement the loop function，
     *                    suggest value 100 to 1000
     *  @param itemSpace  Spacing between two items
     *  @param scale      Left and right item scaling values
     *  @param alpha      The transparency values ​​of the left and right items
     */
    @JvmOverloads
    fun attach(loopParams: Int = 0, itemSpace: Int = 0, scale: Float = 0f, alpha: Float = 0f):
            GalleryHelper {
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
