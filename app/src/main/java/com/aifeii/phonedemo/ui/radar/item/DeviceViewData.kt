package com.aifeii.phonedemo.ui.radar.item

import com.aifeii.phonedemo.R
import com.aifeii.phonedemo.data.model.radar.Device
import com.aifeii.phonedemo.widget.ViewData

/**
 * Created by Jiaming.Luo on 2019/10/18.
 */
class DeviceViewData(private val data: Device) : ViewData<Device> {

    override fun getData(): Device {
        return data
    }

    override fun getLayoutId(): Int {
        return R.layout.view_device
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceViewData

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }


}