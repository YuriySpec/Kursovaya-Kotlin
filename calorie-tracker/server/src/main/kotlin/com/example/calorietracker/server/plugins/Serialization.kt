package com.example.calorietracker.server.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

/**
 * Подключение ContentNegotiation: автоматическое преобразование
 * Kotlin-объектов в JSON и обратно средствами kotlinx.serialization.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}
