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
 *支持recyclerView翻页滑动的工具类
 */
@Suppress("unused")
open class PagingScrollHelper {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mAnimator: ValueAnimator? = null
    private val mOnScrollListener = MyOnScrollListener()
    private var mOnPageChangeListener: OnPageChangeListener? = null
    private val mOnFlingListener = MyOnFlingListener()
    //当前滑动距离
    private var offsetY = 0
    private var offsetX = 0

    //按下屏幕点
    private var startY = 0
    private var startX = 0

    //最后一个可见 view 位置
    private var lastItemPosition = -1
    //第一个可见view的位置
    private var firstItemPosition = -2
    //总 itemView 数量
    private var itemCount: Int = 0
    //滑动至哪一页
    private var currentPage = -1
    //一次滚动 n 页
    private var indexPage: Int = 0

    private var mOrientation = ORIENTATION.HORIZONTAL

    private var mAutoScrollDuration: Long = 5000 // 自动滚动间隔时长

    private var mInnerHandler: InnerHandler? = null

    private var mAutoScrollEnable = false  //是否支持自动滚动

    private val pageIndex: Int //当前滚动到的位置除以屏幕高度的整数就是当前滚动的位置
        get() {
            //获取当前滚动的页数
            return if (mOrientation == ORIENTATION.VERTICAL) {
                offsetY / originHeight
            } else {
                offsetX / originWidth
            }
        }

    private val startPageIndex: Int //当前按下坐标时对应的页数
        get() {
            return if (mOrientation == ORIENTATION.VERTICAL) {
                startY / originHeight
            } else {
                startX / originWidth
            }
        }

    private val originHeight: Int
        get() = mRecyclerView!!.height - mRecyclerView!!.paddingTop - mRecyclerView!!.paddingBottom

    private val originWidth: Int
        get() = mRecyclerView!!.width - mRecyclerView!!.paddingLeft - mRecyclerView!!.paddingRight

    private enum class ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    interface OnPageChangeListener {
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

    fun attachToRecyclerView(recycleView: RecyclerView?, adapter: RecyclerView.Adapter<*>?) {
        requireNotNull(recycleView) { "recycleView must be not null" }
        mRecyclerView = recycleView
        mInnerHandler = InnerHandler(this)
        mAdapter = adapter
        recycleView.onFlingListener = mOnFlingListener
        recycleView.removeOnScrollListener(mOnScrollListener)
        recycleView.addOnScrollListener(mOnScrollListener)
        updateLayoutManger()
    }

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

    fun setOnPageChangeListener(listener: OnPageChangeListener) {
        mOnPageChangeListener = listener
    }

    fun setCurrentPage(page: Int) {
        mRecyclerView!!.scrollToPosition(0)
        updateLayoutManger()
        this.currentPage = page
        mOnFlingListener.onFling(0, 0)
    }

    fun scrollToNextPage(skip: Int = 1) {
        this.indexPage = skip
        mOnFlingListener.onFling(Integer.MAX_VALUE, Integer.MAX_VALUE)
    }

    fun setAutoScroll(enable: Boolean, autoScrollDuration: Long) {
        this.mAutoScrollEnable = enable
        this.mAutoScrollDuration = autoScrollDuration
        mInnerHandler?.removeMessages(1)
        if (enable) {
            mInnerHandler?.sendEmptyMessageDelayed(1, autoScrollDuration)
        }
    }
}