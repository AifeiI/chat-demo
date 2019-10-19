package com.aifeii.phonedemo.ui.radar

import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aifeii.phonedemo.R
import com.aifeii.phonedemo.component.radar.DeviceRadar
import com.aifeii.phonedemo.databinding.ActivityRadarBinding
import com.aifeii.phonedemo.ui.radar.item.DeviceViewData
import com.aifeii.phonedemo.widget.DataBindingRecyclerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RadarActivity : AppCompatActivity() {

    private var scanning: Boolean = false
    private lateinit var adapter: DataBindingRecyclerAdapter<DeviceViewData>
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val localIP = intIP2StringIP(wifiInfo.ipAddress)

        adapter = DataBindingRecyclerAdapter()
        disposable = DeviceRadar.instance.onListener()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                it.ip != localIP
            }
            .doOnNext {
                val newViewData = DeviceViewData(it)
                if (!adapter.contains(newViewData)) {
                    adapter.addViewData(newViewData)
                }
            }
            .subscribe()

        val binding = DataBindingUtil.setContentView<ActivityRadarBinding>(this, R.layout.activity_radar)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.contentLayout.tvLocalIp.text = "Local IP: $localIP"
        binding.contentLayout.recyclerView.adapter = adapter
        binding.contentLayout.btScan.setOnClickListener {
            if (scanning) {
                DeviceRadar.instance.stopScan()
                (it as Button).setText(R.string.text_start_scan)
            } else {
                DeviceRadar.instance.startScan()
                (it as Button).setText(R.string.text_stop_scan)
            }
            scanning = !scanning
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        DeviceRadar.instance.stopScan()
        disposable.dispose()
    }

    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}
