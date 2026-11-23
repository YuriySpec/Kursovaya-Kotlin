package com.example.calorietracker.server.security

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Хеширование и проверка паролей по алгоритму BCrypt со случайной солью.
 * Пароли в базе данных в открытом виде не хранятся.
 */
object PasswordHasher {
    private const val COST = 12

    fun hash(password: String): String =
        BCrypt.withDefaults().hashToString(COST, password.toCharArray())

    fun verify(password: String, hash: String): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hash).verified
}
