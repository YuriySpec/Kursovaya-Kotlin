package com.example.calorietracker.server.domain.repository

import com.example.calorietracker.server.domain.model.FoodEntry
import com.example.calorietracker.server.domain.model.UserProfile
import com.example.calorietracker.server.domain.model.UserRecord

/**
 * Интерфейсы репозиториев (слой бизнес-логики).
 * Реализации, обращающиеся к базе данных через Exposed, находятся в слое данных.
 */

interface UserRepository {
    fun findById(id: Int): UserProfile?
    fun findByUsername(username: String): UserProfile?
    fun findRecordByUsername(username: String): UserRecord?
    fun create(username: String, passwordHash: String): UserProfile
    fun updateGoal(userId: Int, goal: Int): UserProfile
}

interface FoodEntryRepository {
    fun listForDate(userId: Int, date: String): List<FoodEntry>
    fun listForRange(userId: Int, startDate: String, endDate: String): List<FoodEntry>
    fun create(userId: Int, foodName: String, calories: Double, date: String): FoodEntry
    /** Обновляет запись пользователя и возвращает её дату либо null, если записи нет. */
    fun update(userId: Int, entryId: Int, foodName: String, calories: Double): String?
    /** Удаляет запись пользователя и возвращает дату удалённой записи либо null. */
    fun delete(userId: Int, entryId: Int): String?
}
