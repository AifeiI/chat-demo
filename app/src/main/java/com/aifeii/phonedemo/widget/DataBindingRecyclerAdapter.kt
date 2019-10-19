package com.aifeii.phonedemo.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Jiaming.Luo on 2019/10/18.
 */
class DataBindingRecyclerAdapter<T: ViewData<*>> :
    RecyclerView.Adapter<DataBindingRecyclerAdapter.DataBindingViewHolder<T>>() {

    private val viewDataList = ArrayList<T>()

    var onScrollListener: OnScrollListener? = null
    var onItemClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return DataBindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return viewDataList.size
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        val item = viewDataList[position]
        holder.bind(item)
        holder.binding.root.tag = position

        if (onItemClickListener != null) {
            holder.binding.root.setOnClickListener(onItemClickListener)
        }

        if (position == viewDataList.size - 1) {
            onScrollListener?.onBottom()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = viewDataList[position]
        return item.getLayoutId()
    }

    fun addViewData(viewData: T) {
        val oldSize = viewDataList.size
        viewDataList.add(viewData)
        notifyItemInserted(oldSize) // 通知插入新的数据
    }

    fun removeViewData(viewData: T) {
        val size = viewDataList.size
        var position = -1
        for (i in 0..size) {
            if (viewDataList[i] == viewData) {
                position = i
                break
            }
        }
        if (position == -1) {
            return
        }
        notifyItemRemoved(position)      // 通知删除
        viewDataList.removeAt(position)
    }

    fun removeAt(position: Int) {
        val size = viewDataList.size
        if (position >= size || position < 0) {
            return
        }
        notifyItemRemoved(position)
        viewDataList.removeAt(position)
    }

    fun clear() {
        val size = viewDataList.size
        notifyItemRangeRemoved(0, size)
        viewDataList.clear()
    }

    fun get(position: Int): T {
        return viewDataList[position]
    }

    fun contains(dataView: T): Boolean {
        return viewDataList.contains(dataView)
    }

    interface OnScrollListener {

        fun onBottom()

    }

    class DataBindingViewHolder<T: ViewData<*>>(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataView: T) {
            dataView.bind(binding)
        }
    }
}