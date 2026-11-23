package com.example.calorietracker.server.db

import com.example.calorietracker.server.db.table.FoodEntriesTable
import com.example.calorietracker.server.db.table.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Подключение к базе данных.
 * Соединения выдаются через пул HikariCP, схема создаётся при старте
 * сервера средствами Exposed (SchemaUtils.create).
 *
 * В развёрнутой среде используется PostgreSQL: достаточно заменить
 * драйвер и строку подключения (jdbc:postgresql://host:5432/calories),
 * код слоя данных при этом не меняется. Значения по умолчанию
 * соответствуют локальному запуску для отладки.
 */
object DatabaseFactory {

    private const val DB_DRIVER = "org.sqlite.JDBC"
    private const val DB_URL = "jdbc:sqlite:calories.db"

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun now(): String = LocalDateTime.now().format(timeFormatter)

    fun init(): Database {
        val config = HikariConfig().apply {
            driverClassName = DB_DRIVER
            jdbcUrl = DB_URL
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
            connectionInitSql = "PRAGMA foreign_keys = ON"
            validate()
        }
        val dataSource = HikariDataSource(config)
        val database = Database.connect(dataSource)

        transaction(database) {
            SchemaUtils.create(UsersTable, FoodEntriesTable)
        }
        return database
    }
}
