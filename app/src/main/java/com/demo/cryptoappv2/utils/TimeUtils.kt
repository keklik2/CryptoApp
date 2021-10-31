package com.demo.cryptoappv2.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun convertTimestampToTime(time: Long?): String {
    time?.let { val stamp = Timestamp(time * 1000)
        val date = Date(stamp.time)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date) }
    return ""
}