package com.example.calorietracker.server.di

import com.example.calorietracker.server.data.repository.FoodEntryRepositoryImpl
import com.example.calorietracker.server.data.repository.UserRepositoryImpl
import com.example.calorietracker.server.db.DatabaseFactory
import com.example.calorietracker.server.domain.repository.FoodEntryRepository
import com.example.calorietracker.server.domain.repository.UserRepository
import com.example.calorietracker.server.domain.service.AuthService
import com.example.calorietracker.server.domain.service.DiaryService
import org.jetbrains.exposed.sql.Database

/**
 * Контейнер зависимостей приложения.
 * Слой бизнес-логики зависит только от интерфейсов репозиториев;
 * конкретные реализации передаются здесь, при сборке контейнера.
 */
class AppContainer {

    private val database: Database = DatabaseFactory.init()

    // Слой данных
    val userRepository: UserRepository = UserRepositoryImpl(database)
    val foodEntryRepository: FoodEntryRepository = FoodEntryRepositoryImpl(database)

    // Слой бизнес-логики
    val authService = AuthService(userRepository)
    val diaryService = DiaryService(foodEntryRepository, userRepository)
}
