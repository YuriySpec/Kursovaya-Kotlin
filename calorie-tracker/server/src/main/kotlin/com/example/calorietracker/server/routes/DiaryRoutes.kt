package com.example.calorietracker.server.routes

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.domain.service.DiaryService
import com.example.calorietracker.server.dto.CreateEntryRequest
import com.example.calorietracker.server.dto.UpdateEntryRequest
import com.example.calorietracker.server.dto.toDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import java.time.LocalDate

/**
 * Маршруты дневника питания.
 * GET    /api/entries        — записи за дату с суммами;
 * POST   /api/entries        — добавление записи;
 * PUT    /api/entries/{id}   — редактирование записи;
 * DELETE /api/entries/{id}   — удаление записи.
 */
fun Route.diaryRoutes(service: DiaryService) {
    get("/entries") {
        val principal = call.user()
        val date = call.request.queryParameters["date"] ?: LocalDate.now().toString()
        call.respond(service.day(principal.id, date).toDto())
    }
    post("/entries") {
        val principal = call.user()
        val request = call.receive<CreateEntryRequest>()
        val diary = service.add(
            userId = principal.id,
            foodName = request.foodName,
            calories = request.calories,
            date = request.date
        )
        call.respond(HttpStatusCode.Created, diary.toDto())
    }
    put("/entries/{id}") {
        val principal = call.user()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw ApiException.badRequest("Некорректный идентификатор записи")
        val request = call.receive<UpdateEntryRequest>()
        val diary = service.update(
            userId = principal.id,
            entryId = id,
            foodName = request.foodName,
            calories = request.calories
        )
        call.respond(diary.toDto())
    }
    delete("/entries/{id}") {
        val principal = call.user()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw ApiException.badRequest("Некорректный идентификатор записи")
        service.delete(principal.id, id)
        call.respond(HttpStatusCode.NoContent)
    }
}
