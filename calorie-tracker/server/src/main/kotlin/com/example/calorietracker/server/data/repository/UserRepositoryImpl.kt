package com.example.calorietracker.server.data.repository

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.db.DatabaseFactory
import com.example.calorietracker.server.db.table.UsersTable
import com.example.calorietracker.server.domain.model.UserProfile
import com.example.calorietracker.server.domain.model.UserRecord
import com.example.calorietracker.server.domain.repository.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/** Реализация репозитория пользователей на основе Exposed. */
class UserRepositoryImpl(private val database: Database) : UserRepository {

    override fun findById(id: Int): UserProfile? = transaction(database) {
        UsersTable.selectAll().where { UsersTable.id eq id }.firstOrNull()?.toProfile()
    }

    override fun findByUsername(username: String): UserProfile? = transaction(database) {
        UsersTable.selectAll().where { UsersTable.username eq username }.firstOrNull()?.toProfile()
    }

    override fun findRecordByUsername(username: String): UserRecord? = transaction(database) {
        UsersTable.selectAll().where { UsersTable.username eq username }.firstOrNull()?.let { row ->
            UserRecord(row.toProfile(), row[UsersTable.passwordHash])
        }
    }

    override fun create(username: String, passwordHash: String): UserProfile = transaction(database) {
        val now = DatabaseFactory.now()
        val id = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.dailyCalorieGoal] = 2000
            it[createdAt] = now
        } get UsersTable.id
        UserProfile(id, username, 2000, now)
    }

    override fun updateGoal(userId: Int, goal: Int): UserProfile = transaction(database) {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.dailyCalorieGoal] = goal
        }
        UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()?.toProfile()
            ?: throw ApiException.notFound("Пользователь не найден")
    }

    private fun ResultRow.toProfile() = UserProfile(
        id = this[UsersTable.id],
        username = this[UsersTable.username],
        dailyCalorieGoal = this[UsersTable.dailyCalorieGoal],
        createdAt = this[UsersTable.createdAt]
    )
}
