package com.example.analytics

import android.app.Application
import com.example.analytics.library.Analytics
import com.example.analytics.library.AnalyticsBuilder
import kotlinx.coroutines.GlobalScope

class Application: Application() {
    lateinit var analytics: Analytics

    override fun onCreate() {
        super.onCreate()

        AnalyticsBuilder(GlobalScope).apply {
            version = BuildConfig.VERSION_NAME
            analytics = build()
        }
    }
}