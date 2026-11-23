package com.example.calorietracker.data.remote

import com.example.calorietracker.data.remote.dto.CreateEntryRequest
import com.example.calorietracker.data.remote.dto.DayDiaryDto
import com.example.calorietracker.data.remote.dto.RegisterRequest
import com.example.calorietracker.data.remote.dto.UpdateEntryRequest
import com.example.calorietracker.data.remote.dto.UpdateProfileRequest
import com.example.calorietracker.data.remote.dto.UserDto
import com.example.calorietracker.data.remote.dto.WeekStatsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface CalorieApi {

    @POST("api/register")
    suspend fun register(@Body body: RegisterRequest): UserDto

    @POST("api/login")
    suspend fun login(): UserDto

    @GET("api/profile")
    suspend fun profile(): UserDto

    @PUT("api/profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): UserDto

    @GET("api/entries")
    suspend fun entries(@Query("date") date: String): DayDiaryDto

    @POST("api/entries")
    suspend fun addEntry(@Body body: CreateEntryRequest): DayDiaryDto

    @PUT("api/entries/{id}")
    suspend fun updateEntry(@Path("id") id: Int, @Body body: UpdateEntryRequest): DayDiaryDto

    @DELETE("api/entries/{id}")
    suspend fun deleteEntry(@Path("id") id: Int): Response<Unit>

    @GET("api/stats/week")
    suspend fun weekStats(@Query("start") start: String): WeekStatsDto
}
