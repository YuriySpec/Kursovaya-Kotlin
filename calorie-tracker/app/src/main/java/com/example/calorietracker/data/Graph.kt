package com.example.calorietracker.data

import android.content.Context
import com.example.calorietracker.data.remote.CalorieApi
import com.example.calorietracker.data.remote.ApiClient


object Graph {

    lateinit var session: SessionStore
        private set

    val api: CalorieApi by lazy { ApiClient.create(session) }

    val repository: com.example.calorietracker.data.CalorieRepository by lazy {
        CalorieRepository(api)
    }

    fun init(context: Context) {
        session = SessionStore(context)
    }
}
