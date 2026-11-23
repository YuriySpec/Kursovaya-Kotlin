package com.example.calorietracker.server.routes

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.domain.repository.UserRepository
import com.example.calorietracker.server.dto.UpdateProfileRequest
import com.example.calorietracker.server.dto.toDto
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put

/**
 * Маршруты профиля.
 * GET /api/profile — сведения о пользователе и дневная цель;
 * PUT /api/profile — изменение дневной цели по калориям.
 */
fun Route.profileRoutes(users: UserRepository) {
    get("/profile") {
        val principal = call.user()
        val profile = users.findById(principal.id)
            ?: throw ApiException.unauthorized("Пользователь не найден")
        call.respond(profile.toDto())
    }
    put("/profile") {
        val principal = call.user()
        val request = call.receive<UpdateProfileRequest>()
        val goal = request.dailyCalorieGoal
        if (goal < 800 || goal > 10000) {
            throw ApiException.badRequest("Дневная норма должна быть в диапазоне от 800 до 10000 ккал")
        }
        call.respond(users.updateGoal(principal.id, goal).toDto())
    }
}
