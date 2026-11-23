package com.example.calorietracker.data.remote.dto

import kotlinx.serialization.Serializable

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
data class ErrorResponseDto(val message: String? = null)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val dailyCalorieGoal: Int,
    val createdAt: String? = null
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
    val percentOfGoal: Double = 0.0,
    val remaining: Double = 0.0
)

@Serializable
data class DayDiaryDto(
    val date: String,
    val entries: List<EntryDto> = emptyList(),
    val totals: TotalsDto? = null
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
    val days: List<WeekStatDto> = emptyList(),
    val averageCalories: Double = 0.0,
    val maxDay: WeekStatDto? = null
)
