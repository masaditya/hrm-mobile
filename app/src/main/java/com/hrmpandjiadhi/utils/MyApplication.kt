package com.hrmpandjiadhi.utils

import android.app.Application
import com.hrmpandjiadhi.data.api.RetrofitBuilder

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitBuilder.init(this)
    }
}
