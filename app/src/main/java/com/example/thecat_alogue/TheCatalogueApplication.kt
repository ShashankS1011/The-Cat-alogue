package com.example.thecat_alogue

import android.app.Application
import com.google.android.material.color.DynamicColors

class TheCatalogueApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
