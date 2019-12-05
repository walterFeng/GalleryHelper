package com.walter.gallery

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.View

/**
 * Created by walter on 2019/12/4.
 * Email: fengxiao1493@qq.com
 * Tool that supports RecyclerView item zoom when sliding
 * 支持RecyclerView的Item随着滑动时缩放的工具
 */
class ScaleTransformHelper {

    private var scaleScrollListener: ScaleScrollListener? = null

    private var layoutChangeListener: LayoutChangeListener? = null

    private var orientation: Int = RecyclerView.HORIZONTAL

    private var mRecyclerView: RecyclerView? = null

    /**
     *  @param recyclerView recyclerView you want to support
     *  @param scale Left and right item scaling values
     *  @param alpha The transparency values ​​of the left and right items
     */
    fun attachToRecyclerView(recyclerView: RecyclerView, scale: Float, alpha: Float) {
        detachedToRecyclerView()

        this.mRecyclerView = recyclerView
        orientation = when {
            recyclerView.layoutManager!!.canScrollVertically() -> RecyclerView.VERTICAL
            recyclerView.layoutManager!!.canScrollHorizontally() -> RecyclerView.HORIZONTAL
            else -> RecyclerView.HORIZONTAL
        }

        layoutChangeListener = LayoutChangeListener(recyclerView, scale, alpha)
        scaleScrollListener = ScaleScrollListener(scale, alpha)
        recyclerView.addOnScrollListener(scaleScrollListener!!)
        recyclerView.addOnLayoutChangeListener(layoutChangeListener)
    }

    fun detachedToRecyclerView() {
        if (layoutChangeListener != null) {
            mRecyclerView?.removeOnLayoutChangeListener(layoutChangeListener)
        }
        if (scaleScrollListener != null) {
            mRecyclerView?.removeOnScrollListener(scaleScrollListener!!)
        }
    }

    inner class ScaleScrollListener(var scale: Float = 1f, var alpha: Float = 1f) :
        RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val childCount = recyclerView.childCount
            val padding = if (isHorizontal()) recyclerView.paddingLeft else recyclerView.paddingTop
            (0 until childCount).forEach {
                val view = recyclerView.getChildAt(it)
                val size = if (isHorizontal()) view.width else view.height
                val parentSize = if (isHorizontal()) recyclerView.width else recyclerView.height
                val excursion = if (isHorizontal()) view.left else view.top
                var rate = 0f
                if (excursion <= padding) {
                    rate = if (excursion >= padding - size)
                        ((padding - excursion) * 1f / size) else 1f
                    scaleItem(view, 1 - rate * (1 - scale))
                    view.alpha = 1 - rate * (1 - alpha)
                } else {
                    if (excursion <= parentSize - padding) {
                        rate = (parentSize - padding - excursion) * 1f / size
                    }
                    scaleItem(view, scale + rate * (1 - scale))
                    view.alpha = alpha + rate * (1 - alpha)
                }
            }
        }
    }

    inner class LayoutChangeListener(
        var recyclerView: RecyclerView? = null,
        var scale: Float = 1f,
        var alpha: Float = 1f
    ) :
        View.OnLayoutChangeListener {

        override fun onLayoutChange(
            v: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
            val adapter = recyclerView!!.adapter as Adapter
            if (recyclerView!!.childCount < 3) {
                if (recyclerView!!.getChildAt(1) != null) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() % adapter.itemCount == 0) {
                        val v1 = recyclerView!!.getChildAt(1)
                        scaleItem(v1, scale)
                        v1.alpha = alpha
                    } else {
                        val v1 = recyclerView!!.getChildAt(0)
                        scaleItem(v1, scale)
                        v1.alpha = alpha
                    }
                }
            } else {
                if (recyclerView!!.getChildAt(0) != null) {
                    val v0 = recyclerView!!.getChildAt(0)
                    scaleItem(v0, scale)
                    v0.alpha = alpha
                }
                if (recyclerView!!.getChildAt(2) != null) {
                    val v2 = recyclerView!!.getChildAt(2)
                    scaleItem(v2, scale)
                    v2.alpha = alpha
                }
            }
        }
    }

    private fun scaleItem(view: View, value: Float) {
        view.scaleY = if (isHorizontal()) value else 1f
        view.scaleX = if (isHorizontal()) 1f else value
    }

    private fun isHorizontal(): Boolean {
        return orientation == RecyclerView.HORIZONTAL
    }

}