package com.example.calorietracker.server.data.repository

import com.example.calorietracker.server.db.DatabaseFactory
import com.example.calorietracker.server.db.table.FoodEntriesTable
import com.example.calorietracker.server.domain.model.FoodEntry
import com.example.calorietracker.server.domain.repository.FoodEntryRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/** Реализация репозитория дневника питания на основе Exposed. */
class FoodEntryRepositoryImpl(private val database: Database) : FoodEntryRepository {

    override fun listForDate(userId: Int, date: String): List<FoodEntry> = transaction(database) {
        FoodEntriesTable.selectAll().where {
            (FoodEntriesTable.userId eq userId) and (FoodEntriesTable.entryDate eq date)
        }.orderBy(FoodEntriesTable.id to SortOrder.ASC)
            .map { it.toEntry() }
    }

    override fun listForRange(userId: Int, startDate: String, endDate: String): List<FoodEntry> =
        transaction(database) {
            FoodEntriesTable.selectAll().where {
                (FoodEntriesTable.userId eq userId) and
                    (FoodEntriesTable.entryDate greaterEq startDate) and
                    (FoodEntriesTable.entryDate lessEq endDate)
            }.orderBy(FoodEntriesTable.entryDate to SortOrder.ASC, FoodEntriesTable.id to SortOrder.ASC)
                .map { it.toEntry() }
        }

    override fun create(
        userId: Int,
        foodName: String,
        calories: Double,
        date: String
    ): FoodEntry = transaction(database) {
        val id = FoodEntriesTable.insert {
            it[FoodEntriesTable.userId] = userId
            it[FoodEntriesTable.foodName] = foodName
            it[FoodEntriesTable.calories] = calories
            it[entryDate] = date
            it[createdAt] = DatabaseFactory.now()
        } get FoodEntriesTable.id
        FoodEntry(id, foodName, date, calories)
    }

    override fun update(
        userId: Int,
        entryId: Int,
        foodName: String,
        calories: Double
    ): String? = transaction(database) {
        val rows = FoodEntriesTable.update({
            (FoodEntriesTable.id eq entryId) and (FoodEntriesTable.userId eq userId)
        }) {
            it[FoodEntriesTable.foodName] = foodName
            it[FoodEntriesTable.calories] = calories
        }
        if (rows == 0) {
            return@transaction null
        }
        FoodEntriesTable.selectAll().where { FoodEntriesTable.id eq entryId }
            .firstOrNull()?.let { it[FoodEntriesTable.entryDate] }
    }

    override fun delete(userId: Int, entryId: Int): String? = transaction(database) {
        val row = FoodEntriesTable.selectAll().where {
            (FoodEntriesTable.id eq entryId) and (FoodEntriesTable.userId eq userId)
        }.firstOrNull() ?: return@transaction null
        val date = row[FoodEntriesTable.entryDate]
        with(SqlExpressionBuilder) {
            FoodEntriesTable.deleteWhere { FoodEntriesTable.id eq entryId }
        }
        date
    }

    private fun ResultRow.toEntry() = FoodEntry(
        id = this[FoodEntriesTable.id],
        foodName = this[FoodEntriesTable.foodName],
        entryDate = this[FoodEntriesTable.entryDate],
        calories = this[FoodEntriesTable.calories]
    )
}
