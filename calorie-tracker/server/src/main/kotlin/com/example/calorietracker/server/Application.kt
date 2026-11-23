package com.example.calorietracker.server

import com.example.calorietracker.server.di.AppContainer
import com.example.calorietracker.server.plugins.configureSecurity
import com.example.calorietracker.server.plugins.configureSerialization
import com.example.calorietracker.server.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/**
 * Точка входа серверной части приложения «Счётчик калорий».
 * Поднимает встроенный HTTP-сервер Netty на порту 8080 и подключает
 * модуль конфигурации (плагины Ktor, маршруты REST API).
 */
fun main() {
    val container = AppContainer()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(container)
    }.start(wait = true)
}

/** Конфигурация приложения: подключение плагинов и маршрутов. */
fun Application.module(container: AppContainer) {
    configureSerialization()
    configureSecurity(container)
    configureStatusPages()
    configureRouting(container)
}
