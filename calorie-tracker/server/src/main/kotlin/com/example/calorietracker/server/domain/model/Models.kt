package com.example.calorietracker.server.domain.model

/**
 * Модели предметной области серверной части.
 * Слой бизнес-логики не зависит от базы данных и от HTTP-фреймворка.
 */

/** Пользователь приложения. */
data class UserProfile(
    val id: Int,
    val username: String,
    val dailyCalorieGoal: Int,
    val createdAt: String
)

/** Пользователь вместе с хешем пароля (используется только при аутентификации). */
data class UserRecord(
    val profile: UserProfile,
    val passwordHash: String
)

/** Запись дневника питания: название блюда и его калорийность. */
data class FoodEntry(
    val id: Int,
    val foodName: String,
    val entryDate: String,
    val calories: Double
)

/** Суммарные показатели за день. */
data class Totals(
    val calories: Double,
    val goal: Int,
    val percentOfGoal: Double,
    val remaining: Double
)

/** Дневник за конкретную дату. */
data class DayDiary(
    val date: String,
    val entries: List<FoodEntry>,
    val totals: Totals
)

/** Показатели одного дня недели. */
data class WeekDayStat(
    val date: String,
    val calories: Double,
    val goal: Int
)

/** Статистика за неделю. */
data class WeekStats(
    val start: String,
    val days: List<WeekDayStat>,
    val averageCalories: Double,
    val maxDay: WeekDayStat?
)
