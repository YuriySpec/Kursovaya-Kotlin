package com.example.calorietracker.server.common

import io.ktor.http.HttpStatusCode

/**
 * Исключение предметного уровня: несёт HTTP-статус и понятное
 * пользователю сообщение. Перехватывается плагином StatusPages.
 */
class ApiException(
    val status: HttpStatusCode,
    message: String
) : RuntimeException(message) {
    companion object {
        fun badRequest(message: String) = ApiException(HttpStatusCode.BadRequest, message)
        fun unauthorized(message: String) = ApiException(HttpStatusCode.Unauthorized, message)
        fun notFound(message: String) = ApiException(HttpStatusCode.NotFound, message)
        fun conflict(message: String) = ApiException(HttpStatusCode.Conflict, message)
    }
}
