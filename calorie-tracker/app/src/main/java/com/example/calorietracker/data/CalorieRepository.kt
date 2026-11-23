package com.example.calorietracker.data

import com.example.calorietracker.data.remote.CalorieApi
import com.example.calorietracker.data.remote.dto.CreateEntryRequest
import com.example.calorietracker.data.remote.dto.DayDiaryDto
import com.example.calorietracker.data.remote.dto.RegisterRequest
import com.example.calorietracker.data.remote.dto.UpdateEntryRequest
import com.example.calorietracker.data.remote.dto.UpdateProfileRequest
import com.example.calorietracker.data.remote.dto.UserDto
import com.example.calorietracker.data.remote.dto.WeekStatsDto
import retrofit2.Response


class CalorieRepository(private val api: CalorieApi) {

    suspend fun register(username: String, password: String): Result<UserDto> =
        runCatching { api.register(RegisterRequest(username, password)) }

    suspend fun login(): Result<UserDto> =
        runCatching { api.login() }

    suspend fun profile(): Result<UserDto> =
        runCatching { api.profile() }

    suspend fun updateGoal(goal: Int): Result<UserDto> =
        runCatching { api.updateProfile(UpdateProfileRequest(goal)) }

    suspend fun day(date: String): Result<DayDiaryDto> =
        runCatching { api.entries(date) }

    suspend fun addEntry(foodName: String, calories: Double, date: String): Result<DayDiaryDto> =
        runCatching { api.addEntry(CreateEntryRequest(foodName, calories, date)) }

    suspend fun updateEntry(id: Int, foodName: String, calories: Double): Result<DayDiaryDto> =
        runCatching { api.updateEntry(id, UpdateEntryRequest(foodName, calories)) }

    suspend fun deleteEntry(id: Int): Result<Unit> =
        runCatching { requireSuccess(api.deleteEntry(id)) }

    suspend fun week(start: String): Result<WeekStatsDto> =
        runCatching { api.weekStats(start) }

    private fun requireSuccess(response: Response<Unit>) {
        if (!response.isSuccessful) {
            throw com.example.calorietracker.data.remote.HttpError(response.code(), response.errorBody()?.string())
        }
    }
}
