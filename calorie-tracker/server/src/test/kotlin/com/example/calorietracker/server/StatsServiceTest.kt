package com.example.calorietracker.server

import com.example.calorietracker.server.domain.model.FoodEntry
import com.example.calorietracker.server.domain.model.WeekDayStat
import com.example.calorietracker.server.domain.service.StatsService
import kotlin.test.Test
import kotlin.test.assertEquals

/** Тесты расчёта статистики калорийности (слой бизнес-логики). */
class StatsServiceTest {

    private fun entry(calories: Double, name: String = "Блюдо", date: String = "2025-04-01") =
        FoodEntry(
            id = 1, foodName = name, entryDate = date, calories = calories
        )

    @Test
    fun `сумма калорий за день складывается из всех записей`() {
        val totals = StatsService.dayTotals(listOf(entry(300.0), entry(450.0)), 2000)
        assertEquals(750.0, totals.calories)
        assertEquals(37.5, totals.percentOfGoal)
        assertEquals(1250.0, totals.remaining)
    }

    @Test
    fun `превышение цели обнуляет остаток`() {
        val totals = StatsService.dayTotals(listOf(entry(2200.0)), 2000)
        assertEquals(110.0, totals.percentOfGoal)
        assertEquals(0.0, totals.remaining)
    }

    @Test
    fun `пустой день даёт нулевые суммы`() {
        val totals = StatsService.dayTotals(emptyList(), 2000)
        assertEquals(0.0, totals.calories)
        assertEquals(0.0, totals.percentOfGoal)
        assertEquals(2000.0, totals.remaining)
    }

    @Test
    fun `дробная калорийность округляется до одного знака`() {
        val totals = StatsService.dayTotals(listOf(entry(120.5), entry(30.44)), 2000)
        assertEquals(150.9, totals.calories)
    }

    @Test
    fun `среднее за неделю считается корректно`() {
        val days = listOf(
            WeekDayStat("2025-04-07", 1800.0, 2000),
            WeekDayStat("2025-04-08", 2200.0, 2000),
            WeekDayStat("2025-04-09", 2000.0, 2000)
        )
        val stats = StatsService.weekStats("2025-04-07", days)
        assertEquals(2000.0, stats.averageCalories)
        assertEquals("2025-04-08", stats.maxDay?.date)
    }

    @Test
    fun `пустая неделя даёт нулевое среднее`() {
        val stats = StatsService.weekStats("2025-04-07", emptyList())
        assertEquals(0.0, stats.averageCalories)
        assertEquals(null, stats.maxDay)
    }
}
