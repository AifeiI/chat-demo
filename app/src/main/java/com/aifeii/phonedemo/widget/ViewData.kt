package com.aifeii.phonedemo.widget

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.aifeii.phonedemo.BR

/**
 * Created by Jiaming.Luo on 2019/10/18.
 */
interface ViewData<T> {

    @LayoutRes
    fun getLayoutId(): Int

    fun getData(): T

    fun bind(binding: ViewDataBinding) {
        binding.setVariable(BR.item, getData())
    }
}