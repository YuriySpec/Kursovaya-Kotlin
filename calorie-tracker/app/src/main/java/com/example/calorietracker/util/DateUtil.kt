package com.example.calorietracker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Вспомогательные функции работы с датами.
 * Даты передаются серверу в формате ГГГГ-ММ-ДД (ISO),
 * для отображения используется русский формат.
 */
object DateUtil {

    private val ruDisplay: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))

    private val ruShort: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

    fun today(): String = LocalDate.now().toString()

    fun shift(date: String, days: Int): String =
        LocalDate.parse(date).plusDays(days.toLong()).toString()

    fun display(date: String): String = try {
        LocalDate.parse(date).format(ruDisplay)
    } catch (e: Exception) {
        date
    }

    fun shortDisplay(date: String): String = try {
        LocalDate.parse(date).format(ruShort)
    } catch (e: Exception) {
        date
    }

    /** Понедельник текущей недели — начало периода для недельной статистики. */
    fun mondayOfCurrentWeek(): String =
        LocalDate.now().with(DayOfWeek.MONDAY).toString()
}
