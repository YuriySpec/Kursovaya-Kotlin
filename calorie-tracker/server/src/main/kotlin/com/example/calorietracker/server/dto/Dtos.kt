package com.example.calorietracker.server.dto

import com.example.calorietracker.server.domain.model.DayDiary
import com.example.calorietracker.server.domain.model.FoodEntry
import com.example.calorietracker.server.domain.model.Totals
import com.example.calorietracker.server.domain.model.UserProfile
import com.example.calorietracker.server.domain.model.WeekDayStat
import com.example.calorietracker.server.domain.model.WeekStats
import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

/** Объект-аутентифицированный пользователь (заполняется плагином Authentication). */
data class UserPrincipal(val id: Int, val username: String) : Principal

/* ---- Запросы ---- */

@Serializable
data class RegisterRequest(val username: String, val password: String)

@Serializable
data class UpdateProfileRequest(val dailyCalorieGoal: Int)

@Serializable
data class CreateEntryRequest(
    val foodName: String,
    val calories: Double,
    val date: String
)

@Serializable
data class UpdateEntryRequest(
    val foodName: String,
    val calories: Double
)

/* ---- Ответы ---- */

@Serializable
data class ErrorResponse(val message: String)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val dailyCalorieGoal: Int,
    val createdAt: String
)

@Serializable
data class EntryDto(
    val id: Int,
    val foodName: String,
    val date: String,
    val calories: Double
)

@Serializable
data class TotalsDto(
    val calories: Double,
    val goal: Int,
    val percentOfGoal: Double,
    val remaining: Double
)

@Serializable
data class DayDiaryDto(
    val date: String,
    val entries: List<EntryDto>,
    val totals: TotalsDto
)

@Serializable
data class WeekStatDto(
    val date: String,
    val calories: Double,
    val goal: Int
)

@Serializable
data class WeekStatsDto(
    val start: String,
    val days: List<WeekStatDto>,
    val averageCalories: Double,
    val maxDay: WeekStatDto? = null
)

/* ---- Преобразование моделей предметной области в DTO ---- */

fun UserProfile.toDto() = UserDto(id, username, dailyCalorieGoal, createdAt)

fun FoodEntry.toDto() = EntryDto(id, foodName, entryDate, calories)

fun Totals.toDto() = TotalsDto(calories, goal, percentOfGoal, remaining)

fun DayDiary.toDto() = DayDiaryDto(date, entries.map { it.toDto() }, totals.toDto())

fun WeekDayStat.toDto() = WeekStatDto(date, calories, goal)

fun WeekStats.toDto() = WeekStatsDto(start, days.map { it.toDto() }, averageCalories, maxDay?.toDto())
