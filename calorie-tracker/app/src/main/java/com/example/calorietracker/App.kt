package com.example.calorietracker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.calorietracker.data.Graph

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.init(this)
        val mode = if (Graph.session.darkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
