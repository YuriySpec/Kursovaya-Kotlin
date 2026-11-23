package com.example.calorietracker.server.plugins

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.dto.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

/**
 * Централизованная обработка ошибок: бизнес-исключения возвращают
 * клиенту понятное сообщение и корректный HTTP-код,
 * непредвиденные ошибки — код 500 без деталей реализации.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(cause.status, ErrorResponse(cause.message ?: "Ошибка обработки запроса"))
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Непредвиденная ошибка", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Внутренняя ошибка сервера")
            )
        }
    }
}
