package com.example.analytics

import android.app.Application
import com.example.library.Analytics
import com.example.library.analytics
import kotlinx.coroutines.GlobalScope

class Application: Application() {
    lateinit var analytics: Analytics

    override fun onCreate() {
        super.onCreate()

        analytics = analytics(
            scope = GlobalScope,
            version = BuildConfig.VERSION_NAME,
        ) {
            pkg = BuildConfig.APPLICATION_ID
        }

    }
}