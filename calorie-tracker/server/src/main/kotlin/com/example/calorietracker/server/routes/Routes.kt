package com.example.calorietracker.server.routes

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.dto.UserPrincipal
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route

/** Единая точка получения авторизованного пользователя внутри защищённых маршрутов. */
fun ApplicationCall.user(): UserPrincipal =
    principal<UserPrincipal>() ?: throw ApiException.unauthorized("Требуется авторизация")
