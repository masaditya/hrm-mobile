package com.hrmapps.utils

import android.app.Application
import com.hrmapps.data.api.RetrofitBuilder

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitBuilder.init(this)
    }
}
