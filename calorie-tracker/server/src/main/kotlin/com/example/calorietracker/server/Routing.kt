package com.example.calorietracker.server

import com.example.calorietracker.server.di.AppContainer
import com.example.calorietracker.server.routes.authRoutes
import com.example.calorietracker.server.routes.diaryRoutes
import com.example.calorietracker.server.routes.profileRoutes
import com.example.calorietracker.server.routes.statsRoutes
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

/**
 * Подключение всех маршрутов REST API под общим префиксом /api.
 * Регистрация доступна без аутентификации, остальные операции
 * выполняются только для авторизованного пользователя (HTTP Basic).
 */
fun Application.configureRouting(container: AppContainer) {
    routing {
        route("/api") {
            authRoutes(container.authService, container.userRepository)
            authenticate("auth") {
                profileRoutes(container.userRepository)
                diaryRoutes(container.diaryService)
                statsRoutes(container.diaryService)
            }
        }
    }
}
