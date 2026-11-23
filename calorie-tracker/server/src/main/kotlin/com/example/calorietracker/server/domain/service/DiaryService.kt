package com.example.calorietracker.server.domain.service

import com.example.calorietracker.server.common.ApiException
import com.example.calorietracker.server.domain.model.DayDiary
import com.example.calorietracker.server.domain.model.FoodEntry
import com.example.calorietracker.server.domain.repository.FoodEntryRepository
import com.example.calorietracker.server.domain.repository.UserRepository
import java.time.LocalDate

/**
 * Сервис дневника питания: добавление, редактирование и удаление записей,
 * формирование списка записей за день.
 */
class DiaryService(
    private val entries: FoodEntryRepository,
    private val users: UserRepository
) {

    /** Список записей за дату вместе с суммарными показателями. */
    fun day(userId: Int, date: String): DayDiary {
        validateDate(date)
        val goal = users.findById(userId)?.dailyCalorieGoal ?: 2000
        val list = entries.listForDate(userId, date)
        return buildDay(date, list, goal)
    }

    /** Добавление записи о съеденном блюде. */
    fun add(userId: Int, foodName: String, calories: Double, date: String): DayDiary {
        validateDate(date)
        validateFoodName(foodName)
        validateCalories(calories)
        entries.create(userId, foodName.trim(), StatsService.round1(calories), date)
        return day(userId, date)
    }

    /** Редактирование записи дневника: исправление названия и калорийности. */
    fun update(userId: Int, entryId: Int, foodName: String, calories: Double): DayDiary {
        validateFoodName(foodName)
        validateCalories(calories)
        val date = entries.update(userId, entryId, foodName.trim(), StatsService.round1(calories))
            ?: throw ApiException.notFound("Запись не найдена")
        return day(userId, date)
    }

    /** Удаление записи дневника. */
    fun delete(userId: Int, entryId: Int): DayDiary {
        val date = entries.delete(userId, entryId)
            ?: throw ApiException.notFound("Запись не найдена")
        return day(userId, date)
    }

    /** Статистика за 7 дней, начиная с даты start. */
    fun week(userId: Int, start: String): com.example.calorietracker.server.domain.model.WeekStats {
        validateDate(start)
        val startDate = LocalDate.parse(start)
        val endDate = startDate.plusDays(6)
        val goal = users.findById(userId)?.dailyCalorieGoal ?: 2000
        val list = entries.listForRange(userId, startDate.toString(), endDate.toString())
        val byDate = list.groupBy { it.entryDate }
            .mapValues { group -> StatsService.dayTotals(group.value, goal).calories }
        val days = (0..6).map { offset ->
            val date = startDate.plusDays(offset.toLong()).toString()
            com.example.calorietracker.server.domain.model.WeekDayStat(date, byDate[date] ?: 0.0, goal)
        }
        return StatsService.weekStats(start, days)
    }

    companion object {
        /** Проверка формата даты ГГГГ-ММ-ДД и корректности значения. */
        fun validateDate(date: String) {
            val pattern = Regex("^\\d{4}-\\d{2}-\\d{2}$")
            if (!pattern.matches(date)) {
                throw ApiException.badRequest("Дата должна быть в формате ГГГГ-ММ-ДД")
            }
            try {
                LocalDate.parse(date)
            } catch (e: java.time.format.DateTimeParseException) {
                throw ApiException.badRequest("Указана несуществующая дата")
            }
        }

        /** Проверка названия блюда: непустая строка от 1 до 100 символов. */
        fun validateFoodName(name: String) {
            val value = name.trim()
            if (value.isEmpty() || value.length > 100) {
                throw ApiException.badRequest("Название блюда должно содержать от 1 до 100 символов")
            }
        }

        /** Проверка калорийности записи: от 0 до 10000 ккал. */
        fun validateCalories(calories: Double) {
            if (calories.isNaN() || calories < 0.0 || calories > 10000.0) {
                throw ApiException.badRequest("Калорийность должна быть от 0 до 10000 ккал")
            }
        }

        /** Сборка дневника: записи + рассчитанные суммы. */
        fun buildDay(date: String, list: List<FoodEntry>, goal: Int): DayDiary {
            val totals = StatsService.dayTotals(list, goal)
            return DayDiary(date, list, totals)
        }
    }
}
