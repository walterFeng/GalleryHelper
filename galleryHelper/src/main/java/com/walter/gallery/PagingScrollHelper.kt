package com.walter.gallery

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * Created by walter on 2019/12/4.
 * Email: fengxiao1493@qq.com
 * A tool that supports RecyclerView page scrolling
 * 支持recyclerView翻页滑动的工具类
 */
@Suppress("unused")
open class PagingScrollHelper {

    // recycler view params:
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mAnimator: ValueAnimator? = null
    private val mOnScrollListener = MyOnScrollListener()
    private var mOnPageChangeListener: OnPageChangedListener? = null
    private val mOnFlingListener = MyOnFlingListener()
    private var mOrientation = ORIENTATION.HORIZONTAL

    // scroll values:
    private var offsetY = 0   //scroll offset Y
    private var offsetX = 0   //scroll offset X
    private var startY = 0    //event down Y
    private var startX = 0    //event down X
    private var lastItemPosition = -1
    private var firstItemPosition = -2
    private var itemCount: Int = 0  //adapter item count
    private var currentPage = -1    //current page position
    private var indexPage: Int = 0

    // auto scroll:
    private var mAutoScrollDuration: Long = 5000
    private var mInnerHandler: InnerHandler? = null
    private var mAutoScrollEnable = false

    // page index:
    private val pageIndex: Int
        get() {
            return if (mOrientation == ORIENTATION.VERTICAL) {
                offsetY / originHeight
            } else {
                offsetX / originWidth
            }
        }
    private val startPageIndex: Int
        get() {
            return if (mOrientation == ORIENTATION.VERTICAL) {
                startY / originHeight
            } else {
                startX / originWidth
            }
        }


    // page width and height:
    private val originHeight: Int
        get() = mRecyclerView!!.height - mRecyclerView!!.paddingTop - mRecyclerView!!.paddingBottom

    private val originWidth: Int
        get() = mRecyclerView!!.width - mRecyclerView!!.paddingLeft - mRecyclerView!!.paddingRight

    private enum class ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    interface OnPageChangedListener {
        fun onPageChange(index: Int)
    }

    private class InnerHandler internal constructor(outer: PagingScrollHelper) : Handler() {
        private val wTarget: WeakReference<PagingScrollHelper> = WeakReference(outer)

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val pagingScrollHelper = wTarget.get() ?: return
            when (msg?.what) {
                1 -> {
                    pagingScrollHelper.scrollToNextPage()
                    sendEmptyMessageDelayed(1, pagingScrollHelper.mAutoScrollDuration)
                }
            }
        }
    }

    /**
     *  @param recycleView recyclerView you want to support
     *  @param adapter recyclerView adapter
     */
    fun attachToRecyclerView(recycleView: RecyclerView?, adapter: RecyclerView.Adapter<*>?) {
        detachedToRecyclerView()
        requireNotNull(recycleView) { "recycleView must be not null" }
        mRecyclerView = recycleView
        mInnerHandler = InnerHandler(this)
        mAdapter = adapter
        recycleView.onFlingListener = mOnFlingListener
        recycleView.addOnScrollListener(mOnScrollListener)
        updateLayoutManger()
    }

    fun detachedToRecyclerView() {
        mRecyclerView?.removeOnScrollListener(mOnScrollListener)
        mRecyclerView = null
    }

    /**
     *  Need to reset values when updating layoutManager
     */
    fun updateLayoutManger() {
        val layoutManager = mRecyclerView!!.layoutManager
        if (layoutManager != null) {
            mOrientation = when {
                layoutManager.canScrollVertically() -> ORIENTATION.VERTICAL
                layoutManager.canScrollHorizontally() -> ORIENTATION.HORIZONTAL
                else -> ORIENTATION.NULL
            }
            mAnimator?.cancel()
            startX = 0
            startY = 0
            offsetX = 0
            offsetY = 0
        }
    }

    fun setOnPageChangedListener(listener: OnPageChangedListener) {
        mOnPageChangeListener = listener
    }

    /**
     * scroll to page
     */
    fun setCurrentPage(page: Int) {
        mRecyclerView!!.scrollToPosition(0)
        updateLayoutManger()
        this.currentPage = page
        mOnFlingListener.onFling(0, 0)
    }

    /**
     * scroll to next page
     *  @param skip skip page count
     */
    fun scrollToNextPage(skip: Int = 1) {
        this.indexPage = skip
        mOnFlingListener.onFling(Integer.MAX_VALUE, Integer.MAX_VALUE)
    }

    /**
     *  @param enable auto scroll enable
     *  @param autoScrollDuration auto scroll duration
     */
    fun setAutoScroll(enable: Boolean, autoScrollDuration: Long) {
        this.mAutoScrollEnable = enable
        this.mAutoScrollDuration = autoScrollDuration
        mInnerHandler?.removeMessages(1)
        if (enable) {
            mInnerHandler?.sendEmptyMessageDelayed(1, autoScrollDuration)
        }
    }

    private inner class MyOnFlingListener : RecyclerView.OnFlingListener() {

        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            if (mOrientation == ORIENTATION.NULL) {
                return false
            }

            var page = startPageIndex

            val endPoint: Int
            var startPoint: Int

            if (mOrientation == ORIENTATION.VERTICAL) {
                startPoint = offsetY
                when {
                    velocityY == Integer.MAX_VALUE -> page += indexPage
                    velocityY < 0 -> page--
                    velocityY > 0 -> page++
                    currentPage >= 0 -> {
                        startPoint = 0
                        page = currentPage - 1
                    }
                }
                endPoint = page * originHeight

            } else {
                startPoint = offsetX
                when {
                    velocityX == Integer.MAX_VALUE -> page += indexPage
                    velocityX < 0 -> page--
                    velocityX > 0 -> page++
                    currentPage >= 0 -> {
                        startPoint = 0
                        page = currentPage - 1
                    }
                }
                endPoint = page * originWidth
            }
            if (mAnimator == null) {
                mAnimator = ValueAnimator.ofInt(startPoint, endPoint)
                mAnimator!!.duration = 300
                mAnimator!!.addUpdateListener { animation ->
                    if (originWidth <= 0) {
                        return@addUpdateListener
                    }
                    val nowPoint = animation.animatedValue as Int

                    if (mOrientation == ORIENTATION.VERTICAL) {
                        val dy = nowPoint - offsetY
                        if (dy == 0) return@addUpdateListener
                        mRecyclerView!!.scrollBy(0, dy)
                    } else {
                        val dx = nowPoint - offsetX
                        mRecyclerView!!.scrollBy(dx, 0)
                    }
                }
                mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    var isCancel = false
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (isCancel) {
                            isCancel = false
                            return
                        }
                        if (originWidth <= 0) {
                            return
                        }
                        mRecyclerView?.stopScroll()
                        val index = pageIndex
                        mOnPageChangeListener?.onPageChange(index)
                        offsetY = index * originHeight
                        offsetX = index * originWidth
                        startY = offsetY
                        startX = offsetX
                        val layoutManager = mRecyclerView!!.layoutManager
                        if (layoutManager is LinearLayoutManager) {
                            lastItemPosition = layoutManager.findLastVisibleItemPosition()
                            firstItemPosition = layoutManager.findFirstVisibleItemPosition()
                        }
                        itemCount = mAdapter?.itemCount ?: -1
                        if (itemCount == lastItemPosition + 1) {
                            updateLayoutManger()
                        }
                        if (firstItemPosition == 0) {
                            updateLayoutManger()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        super.onAnimationCancel(animation)
                        isCancel = true
                    }
                })
            } else {
                mAnimator!!.cancel()
                mAnimator!!.setIntValues(startPoint, endPoint)
            }

            mAnimator!!.start()

            return true
        }
    }

    private inner class MyOnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mOrientation != ORIENTATION.NULL) {
                val move: Boolean
                var vX = 0
                var vY = 0
                if (mOrientation == ORIENTATION.VERTICAL) {
                    val absY = abs(offsetY - startY)
                    move = absY > originHeight / 2
                    vY = 0
                    if (move) {
                        vY = if (offsetY - startY < 0) -1000 else 1000
                    }
                } else {
                    val absX = abs(offsetX - startX)
                    move = absX > originWidth / 2
                    if (move) {
                        vX = if (offsetX - startX < 0) -1000 else 1000
                    }
                }
                mOnFlingListener.onFling(vX, vY)
            }

            if (mAutoScrollEnable) {
                mInnerHandler?.removeMessages(1)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    mInnerHandler?.sendEmptyMessageDelayed(1, mAutoScrollDuration)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            offsetY += dy
            offsetX += dx
        }
    }
}