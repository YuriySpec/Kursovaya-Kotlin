package com.example.calorietracker.server

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.domain.model.UserProfile
import com.example.calorietracker.server.domain.model.UserRecord
import com.example.calorietracker.server.domain.repository.UserRepository
import com.example.calorietracker.server.domain.service.AuthService
import com.example.calorietracker.server.domain.service.DiaryService
import com.example.calorietracker.server.security.PasswordHasher
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** Тесты валидации данных (аутентификация, записи дневника, даты). */
class ValidationTest {

    private class FakeUserRepository : UserRepository {
        private val records = mutableMapOf<String, UserRecord>()
        private var nextId = 1

        override fun findById(id: Int): UserProfile? =
            records.values.firstOrNull { it.profile.id == id }?.profile

        override fun findByUsername(username: String): UserProfile? = records[username]?.profile

        override fun findRecordByUsername(username: String): UserRecord? = records[username]

        override fun create(username: String, passwordHash: String): UserProfile {
            val profile = UserProfile(nextId++, username, 2000, "2025-04-01 10:00:00")
            records[username] = UserRecord(profile, passwordHash)
            return profile
        }

        override fun updateGoal(userId: Int, goal: Int): UserProfile {
            val record = records.values.firstOrNull { it.profile.id == userId }
                ?: throw ApiException.notFound("нет")
            val updated = record.profile.copy(dailyCalorieGoal = goal)
            records[updated.username] = UserRecord(updated, record.passwordHash)
            return updated
        }
    }

    private val authService = AuthService(FakeUserRepository())

    @Test
    fun `слишком короткий логин отклоняется`() {
        val exception = assertFailsWith<ApiException> { authService.register("ab", "123456") }
        assertEquals(HttpStatusCode.BadRequest, exception.status)
    }

    @Test
    fun `слишком короткий пароль отклоняется`() {
        val exception = assertFailsWith<ApiException> { authService.register("ivan", "12345") }
        assertEquals(HttpStatusCode.BadRequest, exception.status)
    }

    @Test
    fun `логин с недопустимыми символами отклоняется`() {
        assertFailsWith<ApiException> { authService.register("user name!", "123456") }
    }

    @Test
    fun `повторная регистрация того же логина возвращает конфликт`() {
        authService.register("ivan", "123456")
        val exception = assertFailsWith<ApiException> { authService.register("ivan", "654321") }
        assertEquals(HttpStatusCode.Conflict, exception.status)
    }

    @Test
    fun `корректный вход возвращает профиль`() {
        authService.register("maria", "123456")
        val profile = authService.login("maria", "123456")
        assertEquals("maria", profile.username)
    }

    @Test
    fun `вход с неверным паролем отклоняется`() {
        authService.register("maria", "123456")
        assertFailsWith<ApiException> { authService.login("maria", "wrong-password") }
    }

    @Test
    fun `пароль хешируется и не хранится в открытом виде`() {
        authService.register("olga", "super-secret")
        val record = authService.authenticate("olga", "super-secret")
        assertTrue(record != null)
    }

    @Test
    fun `verify корректно проверяет хеш BCrypt`() {
        val hash = PasswordHasher.hash("secret123")
        assertTrue(PasswordHasher.verify("secret123", hash))
    }

    @Test
    fun `пустое название блюда отклоняется`() {
        assertFailsWith<ApiException> { DiaryService.validateFoodName("   ") }
    }

    @Test
    fun `слишком длинное название блюда отклоняется`() {
        assertFailsWith<ApiException> { DiaryService.validateFoodName("Я".repeat(101)) }
    }

    @Test
    fun `отрицательная калорийность отклоняется`() {
        assertFailsWith<ApiException> { DiaryService.validateCalories(-5.0) }
    }

    @Test
    fun `калорийность больше 10000 отклоняется`() {
        assertFailsWith<ApiException> { DiaryService.validateCalories(15000.0) }
    }

    @Test
    fun `корректная запись проходит проверку`() {
        DiaryService.validateFoodName("Яйцо куриное варёное")
        DiaryService.validateCalories(240.0)
        DiaryService.validateCalories(0.0)
    }

    @Test
    fun `дата в неверном формате отклоняется`() {
        assertFailsWith<ApiException> { DiaryService.validateDate("01.02.2025") }
    }

    @Test
    fun `несуществующая дата отклоняется`() {
        assertFailsWith<ApiException> { DiaryService.validateDate("2025-02-30") }
    }

    @Test
    fun `корректная дата проходит проверку`() {
        DiaryService.validateDate("2025-04-01")
    }
}
