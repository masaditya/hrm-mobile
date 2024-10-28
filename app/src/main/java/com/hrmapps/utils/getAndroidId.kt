package com.hrmapps.utils

import android.annotation.SuppressLint
import android.provider.Settings
import android.content.Context

@SuppressLint("HardwareIds")
fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
