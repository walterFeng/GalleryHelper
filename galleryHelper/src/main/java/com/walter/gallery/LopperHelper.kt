package com.walter.gallery

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by walter on 2019/12/4.
 * Email: fengxiao1493@qq.com
 * A Tool that Make recyclerView support circular sliding
 * 使RecyclerView支持循环滑动的工具
 */
@Suppress("unused")
class LopperHelper {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapterWrap: LopperAdapterWrap<*>? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mInitPosition = 0

    /**
     * @param recyclerView detached to the recyclerView
     * @param loop if recyclerView be allowed to loop
     * @param loopParams parameters for looping
     * @param itemSpace If you need to add spacing between item
     */
    fun attachToRecyclerView(
        recyclerView: RecyclerView,
        loop: Boolean,
        loopParams: Int,
        itemSpace: Int = 0
    ) {
        detachedToRecyclerView()
        this.mRecyclerView = recyclerView
        mAdapter = recyclerView.adapter!!
        mAdapterWrap = LopperAdapterWrap(recyclerView, mAdapter!!, loop, loopParams, itemSpace)
        mAdapterWrap?.setHasStableIds(mAdapter!!.hasStableIds())
        recyclerView.adapter = mAdapterWrap
        mInitPosition = if (loop) (loopParams * mAdapter!!.itemCount) / 2 else 0
        recyclerView.scrollToPosition(mInitPosition)
    }

    fun detachedToRecyclerView() {
        mRecyclerView?.adapter = mAdapter
    }

    /**
     * @param loop if recyclerView be allowed to loop
     */
    fun setLoopEnable(loop: Boolean) {
        mAdapterWrap?.loop = loop
        mAdapterWrap?.notifyDataSetChanged()
        mInitPosition = if (loop) (mAdapterWrap!!.loopParams * mAdapter!!.itemCount) / 2 else 0
        mRecyclerView?.scrollToPosition(if (loop) (mAdapterWrap!!.loopParams * mAdapter!!.itemCount) / 2 else 0)
    }

    /**
     * @return true if recyclerView be allowed to loop
     */
    fun isLoopEnable(): Boolean = mAdapterWrap?.loop ?: false

    /**
     * @return the init real position
     */
    fun getInitPosition(): Int = mInitPosition

    /**
     * notify item support
     */
    fun notifyItemChangedSupport(position: Int) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemChanged(gerCurrentNearestPosition(position))
        else
            mAdapterWrap!!.notifyItemChanged(position)
    }

    fun notifyItemChangedSupport(position: Int, payload: Any?) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemChanged(gerCurrentNearestPosition(position), payload)
        else
            mAdapterWrap!!.notifyItemChanged(position, payload)
    }

    fun notifyItemRangeChangedSupport(positionStart: Int, itemCount: Int) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemRangeChanged(
                gerCurrentNearestPosition(positionStart),
                itemCount
            )
        else
            mAdapterWrap!!.notifyItemRangeChanged(positionStart, itemCount)
    }

    fun notifyItemRangeChangedSupport(positionStart: Int, itemCount: Int, payload: Any?) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemRangeChanged(
                gerCurrentNearestPosition(positionStart),
                itemCount,
                payload
            )
        else
            mAdapterWrap!!.notifyItemRangeChanged(positionStart, itemCount, payload)
    }

    fun notifyItemInsertedSupport(position: Int) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemInserted(gerCurrentNearestPosition(position))
        else
            mAdapterWrap!!.notifyItemInserted(position)
    }

    fun notifyItemMovedSupport(fromPosition: Int, toPosition: Int) {
        if (mAdapterWrap!!.loop) {
            val from = gerCurrentNearestPosition(fromPosition)
            val to = toPosition - fromPosition + from
            mAdapterWrap!!.notifyItemMoved(from, to)
        } else {
            mAdapterWrap!!.notifyItemMoved(fromPosition, toPosition)
        }
    }

    fun notifyItemRangeInsertedSupport(positionStart: Int, itemCount: Int) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemRangeInserted(
                gerCurrentNearestPosition(positionStart),
                itemCount
            )
        else
            mAdapterWrap!!.notifyItemRangeInserted(positionStart, itemCount)
    }

    fun notifyItemRemovedSupport(position: Int) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemRemoved(gerCurrentNearestPosition(position))
        else
            mAdapterWrap!!.notifyItemRemoved(position)
    }

    fun notifyItemRangeRemovedSupport(positionStart: Int, itemCount: Int) {
        if (mAdapterWrap!!.loop)
            mAdapterWrap!!.notifyItemRangeRemoved(
                gerCurrentNearestPosition(positionStart),
                itemCount
            )
        else
            mAdapterWrap!!.notifyItemRangeRemoved(positionStart, itemCount)
    }

    /**
     * @return current real position for AdapterWrap
     */
    fun gerCurrentRealPosition(): Int {
        return (mRecyclerView!!.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    /**
     * @return reset position
     */
    fun resetPosition() {
        if (mAdapterWrap?.loop == true) {
            val position = mInitPosition + (gerCurrentRealPosition() % mAdapter!!.itemCount)
            mRecyclerView?.scrollToPosition(position)
        }
    }

    /**
     * @return current nearest position for AdapterWrap
     * @param position your adapter position
     */
    fun gerCurrentNearestPosition(position: Int): Int {
        if (!mAdapterWrap!!.loop) {
            return position
        }
        val current = gerCurrentRealPosition()
        val mapping = current % mAdapter!!.itemCount
        val between = position - mapping
        var result = current + between
        if (result >= mAdapterWrap!!.itemCount || result < 0) {
            result = current
        }
        return result
    }

    /**
     * Adapter that supports circular scrolling
     */
    class LopperAdapterWrap<VH : RecyclerView.ViewHolder>(
        recyclerView: RecyclerView, private val adapter: RecyclerView.Adapter<VH>,
        var loop: Boolean, val loopParams: Int, private val space: Int = 0
    ) : RecyclerView.Adapter<VH>() {

        private val orientation = when {
            recyclerView.layoutManager!!.canScrollVertically() -> RecyclerView.VERTICAL
            recyclerView.layoutManager!!.canScrollHorizontally() -> RecyclerView.HORIZONTAL
            else -> RecyclerView.VERTICAL
        }
        private val spaceValue = (space / 2f).toInt()

        fun getRealAdapter(): RecyclerView.Adapter<out RecyclerView.ViewHolder> = adapter

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): VH {
            val index = position % (adapter.itemCount)
            val viewHolder = adapter.onCreateViewHolder(parent, index)

            val params = viewHolder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = if (orientation == RecyclerView.HORIZONTAL) spaceValue else 0
            params.rightMargin = if (orientation == RecyclerView.HORIZONTAL) spaceValue else 0
            params.topMargin = if (orientation == RecyclerView.HORIZONTAL) 0 else spaceValue
            params.bottomMargin = if (orientation == RecyclerView.HORIZONTAL) 0 else spaceValue

            return viewHolder
        }

        override fun getItemCount(): Int {
            return if (loop) loopParams * (adapter.itemCount) else adapter.itemCount
        }

        override fun onBindViewHolder(viewHolder: VH, position: Int) {
            val index = position % (adapter.itemCount)
            adapter.onBindViewHolder(viewHolder, index)
        }

        override fun getItemId(position: Int): Long {
            val index = position % (adapter.itemCount)
            return adapter.getItemId(index)
        }

        override fun getItemViewType(position: Int): Int {
            val index = position % (adapter.itemCount)
            return adapter.getItemViewType(index)
        }
    }
}