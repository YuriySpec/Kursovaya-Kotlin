package com.example.calorietracker.server.routes

import com.example.calorietracker.server.domain.service.DiaryService
import com.example.calorietracker.server.dto.toDto
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Маршрут статистики.
 * GET /api/stats/week?start=ГГГГ-ММ-ДД — калорийность по дням за неделю,
 * среднее значение и самый калорийный день. Без параметра возвращается
 * текущая неделя (с понедельника).
 */
fun Route.statsRoutes(service: DiaryService) {
    get("/stats/week") {
        val principal = call.user()
        val start = call.request.queryParameters["start"]
            ?: LocalDate.now().with(DayOfWeek.MONDAY).format(DateTimeFormatter.ISO_LOCAL_DATE)
        call.respond(service.week(principal.id, start).toDto())
    }
}
