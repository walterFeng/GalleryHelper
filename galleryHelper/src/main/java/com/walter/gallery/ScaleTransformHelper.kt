package com.walter.gallery

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.View

class ScaleTransformHelper {

    private val scaleScrollListener: ScaleScrollListener = ScaleScrollListener(0f, 0f)

    class ScaleScrollListener(var scale: Float, var alpha: Float) : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val childCount = recyclerView.childCount
            val padding = recyclerView.paddingLeft
            (0 until childCount).forEach {
                val v = recyclerView.getChildAt(it)
                var rate = 0f
                if (v.left <= padding) {
                    rate = if (v.left >= padding - v.width) {
                        (padding - v.left) * 1f / v.width
                    } else {
                        1f
                    }
                    v.scaleY = 1 - rate * (1 - scale)
                    v.alpha = 1 - rate * (1 - alpha)
                } else {
                    if (v.left <= recyclerView.width - padding) {
                        rate = (recyclerView.width - padding - v.left) * 1f / v.width
                    }
                    v.scaleY = scale + rate * (1 - scale)
                    v.alpha = alpha + rate * (1 - alpha)
                }
            }
        }
    }

    private var layoutChangeListener: LayoutChangeListener? = null

    class LayoutChangeListener(var recyclerView: RecyclerView, var scale: Float, var alpha: Float) : View.OnLayoutChangeListener {

        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val adapter: Adapter<RecyclerView.ViewHolder> = recyclerView.adapter as Adapter

        override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
            if (recyclerView.childCount < 3) {
                if (recyclerView.getChildAt(1) != null) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() % adapter.itemCount == 0) {
                        val v1 = recyclerView.getChildAt(1)
                        v1.scaleY = scale
                        v1.alpha = alpha
                    } else {
                        val v1 = recyclerView.getChildAt(0)
                        v1.scaleY = scale
                        v1.alpha = alpha
                    }
                }
            } else {
                if (recyclerView.getChildAt(0) != null) {
                    val v0 = recyclerView.getChildAt(0)
                    v0.scaleY = scale
                    v0.alpha = alpha
                }
                if (recyclerView.getChildAt(2) != null) {
                    val v2 = recyclerView.getChildAt(2)
                    v2.scaleY = scale
                    v2.alpha = alpha
                }
            }
        }
    }

    fun attachToRecyclerView(recyclerView: RecyclerView, scale: Float, alpha: Float) {
        scaleScrollListener.alpha = alpha
        scaleScrollListener.scale = scale
        recyclerView.removeOnScrollListener(scaleScrollListener)
        recyclerView.addOnScrollListener(scaleScrollListener)
        recyclerView.removeOnLayoutChangeListener(layoutChangeListener)
        layoutChangeListener = LayoutChangeListener(recyclerView, scale, alpha)
        recyclerView.addOnLayoutChangeListener(layoutChangeListener)
    }

}