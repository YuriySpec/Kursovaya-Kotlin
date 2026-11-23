package com.example.calorietracker.server.domain.service

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.domain.model.UserProfile
import com.example.calorietracker.server.domain.repository.UserRepository
import com.example.calorietracker.server.security.PasswordHasher

/**
 * Сервис аутентификации: регистрация, вход, проверка учётных данных.
 * Содержит правила валидации данных пользователя.
 */
class AuthService(private val users: UserRepository) {

    /** Проверка учётных данных при обращении к защищённым маршрутам. */
    fun authenticate(username: String, password: String): UserProfile? {
        val record = users.findRecordByUsername(username) ?: return null
        return if (PasswordHasher.verify(password, record.passwordHash)) record.profile else null
    }

    /** Регистрация новой учётной записи. */
    fun register(username: String, password: String): UserProfile {
        validateUsername(username)
        validatePassword(password)
        if (users.findByUsername(username) != null) {
            throw ApiException.conflict("Пользователь с таким логином уже существует")
        }
        return users.create(username.trim(), PasswordHasher.hash(password))
    }

    /** Вход в учётную запись. */
    fun login(username: String, password: String): UserProfile {
        val record = users.findRecordByUsername(username.trim())
            ?: throw ApiException.unauthorized("Неверный логин или пароль")
        if (!PasswordHasher.verify(password, record.passwordHash)) {
            throw ApiException.unauthorized("Неверный логин или пароль")
        }
        return record.profile
    }

    private fun validateUsername(username: String) {
        val value = username.trim()
        if (value.length < 3 || value.length > 50) {
            throw ApiException.badRequest("Логин должен содержать от 3 до 50 символов")
        }
        val pattern = Regex("^[A-Za-z0-9_.\\-а-яА-ЯёЁ]+$")
        if (!pattern.matches(value)) {
            throw ApiException.badRequest("Логин может содержать только буквы, цифры, точку, дефис и подчёркивание")
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 6 || password.length > 100) {
            throw ApiException.badRequest("Пароль должен содержать от 6 до 100 символов")
        }
    }
}
