package com.example.calorietracker.data

import android.content.Context
import android.content.SharedPreferences


class SessionStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var login: String?
        get() = prefs.getString(KEY_LOGIN, null)
        private set(value) = prefs.edit().putString(KEY_LOGIN, value).apply()

    var password: String?
        get() = prefs.getString(KEY_PASSWORD, null)
        private set(value) = prefs.edit().putString(KEY_PASSWORD, value).apply()

    /** Признак включённой тёмной темы. */
    var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    fun save(login: String, password: String) {
        prefs.edit()
            .putString(KEY_LOGIN, login)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun isAuthorized(): Boolean = !login.isNullOrEmpty() && !password.isNullOrEmpty()

    fun clear() {
        prefs.edit().remove(KEY_LOGIN).remove(KEY_PASSWORD).apply()
    }

    private companion object {
        const val PREFS_NAME = "calorie_session"
        const val KEY_LOGIN = "login"
        const val KEY_PASSWORD = "password"
        const val KEY_DARK_THEME = "dark_theme"
    }
}
