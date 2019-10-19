package com.aifeii.phonedemo

import android.app.Application
import com.aifeii.phonedemo.component.radar.DeviceRadar

/**
 * Created by Jiaming.Luo on 2019/10/18.
 */
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        DeviceRadar.instance.start()
    }

}