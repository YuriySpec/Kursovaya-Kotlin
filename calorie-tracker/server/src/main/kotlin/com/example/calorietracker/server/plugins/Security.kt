package com.example.calorietracker.server.plugins

import com.example.calorietracker.server.di.AppContainer
import com.example.calorietracker.server.dto.UserPrincipal
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.basic

/**
 * Подключение аутентификации по схеме HTTP Basic.
 * Клиент передаёт логин и пароль в заголовке Authorization,
 * сервер сверяет пароль с сохранённым хешем (BCrypt) и допускает
 * пользователя только к его собственным данным.
 */
fun Application.configureSecurity(container: AppContainer) {
    install(Authentication) {
        basic("auth") {
            realm = "CalorieTracker"
            validate { credentials: UserPasswordCredential ->
                container.authService
                    .authenticate(credentials.name, credentials.password)
                    ?.let { UserPrincipal(it.id, it.username) }
            }
        }
    }
}
