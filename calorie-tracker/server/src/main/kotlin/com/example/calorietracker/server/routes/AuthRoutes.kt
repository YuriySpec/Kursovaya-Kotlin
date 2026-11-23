package com.example.calorietracker.server.routes

import com.example.calorietracker.server.domain.repository.UserRepository
import com.example.calorietracker.server.domain.service.AuthService
import com.example.calorietracker.server.dto.RegisterRequest
import com.example.calorietracker.server.dto.toDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

/**
 * Маршруты аутентификации.
 * POST /api/register — регистрация новой учётной записи;
 * POST /api/login     — вход (учётные данные передаются в заголовке Authorization).
 */
fun Route.authRoutes(auth: AuthService, users: UserRepository) {
    post("/register") {
        val request = call.receive<RegisterRequest>()
        val user = auth.register(request.username, request.password)
        call.respond(HttpStatusCode.Created, user.toDto())
    }
    authenticate("auth") {
        post("/login") {
            val principal = call.user()
            val profile = users.findById(principal.id)
                ?: throw com.example.calorietracker.server.common.ApiException.unauthorized("Пользователь не найден")
            call.respond(profile.toDto())
        }
    }
}
