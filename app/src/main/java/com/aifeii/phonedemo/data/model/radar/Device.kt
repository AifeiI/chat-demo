package com.aifeii.phonedemo.data.model.radar

/**
 * Created by Jiaming.Luo on 2019/10/18.
 */
data class Device(
    val ip: String = "",
    val port: Int = 0,
    val mac: String = "",
    val name: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Device

        if (ip != other.ip) return false
        if (mac != other.mac) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.hashCode()
        result = 31 * result + mac.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}