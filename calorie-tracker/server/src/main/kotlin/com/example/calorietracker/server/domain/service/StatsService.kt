package com.example.calorietracker.server.domain.service

import com.example.calorietracker.server.domain.model.FoodEntry
import com.example.calorietracker.server.domain.model.Totals
import com.example.calorietracker.server.domain.model.WeekDayStat
import com.example.calorietracker.server.domain.model.WeekStats
import kotlin.math.roundToInt

/**
 * Расчёт статистики калорийности.
 * Чистые функции без обращения к базе данных — покрываются unit-тестами.
 */
object StatsService {

    /** Суммарная калорийность за день и прогресс относительно дневной цели. */
    fun dayTotals(entries: List<FoodEntry>, goal: Int): Totals {
        val calories = round1(entries.sumOf { it.calories })
        val percent = if (goal <= 0) 0.0 else round1(calories / goal * 100.0)
        val remaining = round1((goal - calories).coerceAtLeast(0.0))
        return Totals(calories, goal, percent, remaining)
    }

    /** Статистика за неделю: среднее значение и самый калорийный день. */
    fun weekStats(start: String, days: List<WeekDayStat>): WeekStats {
        val average = if (days.isEmpty()) 0.0 else round1(days.sumOf { it.calories } / days.size)
        return WeekStats(start, days, average, days.maxByOrNull { it.calories })
    }

    fun round1(value: Double): Double = (value * 10).roundToInt() / 10.0
}
