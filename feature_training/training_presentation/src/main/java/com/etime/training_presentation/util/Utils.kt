package com.etime.training_presentation.util

import android.content.Context
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getTimeStamp(): String{

    val timestamp: Long = System.currentTimeMillis()

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp

    val strDate = formatter.format(calendar.time)

    return strDate
}

fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}