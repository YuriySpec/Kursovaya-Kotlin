package com.example.calorietracker.server.db.table

import org.jetbrains.exposed.sql.Table

/**
 * Описание таблиц базы данных средствами Exposed.
 * Структура соответствует логической модели базы данных приложения.
 */

/** Пользователи приложения. */
object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    val dailyCalorieGoal = integer("daily_calorie_goal").default(2000)
    val createdAt = varchar("created_at", 32)

    override val primaryKey = PrimaryKey(id)
}

/** Дневник питания: записи о съеденных блюдах. */
object FoodEntriesTable : Table("food_entries") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val foodName = varchar("food_name", 100)
    val calories = double("calories")
    val entryDate = varchar("entry_date", 10)
    val createdAt = varchar("created_at", 32)

    override val primaryKey = PrimaryKey(id)
}
