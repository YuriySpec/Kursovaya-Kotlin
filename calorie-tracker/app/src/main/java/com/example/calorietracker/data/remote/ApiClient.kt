package com.example.calorietracker.data.remote

import com.example.calorietracker.data.SessionStore
import com.example.calorietracker.data.remote.dto.ErrorResponseDto
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

fun httpErrorMessage(code: Int, errorBody: String?): String {
    return try {
        errorBody?.let {
            Json { ignoreUnknownKeys = true }
                .decodeFromString<ErrorResponseDto>(it)
                .message
        } ?: "Ошибка сервера (код $code)"
    } catch (e: Exception) {
        "Ошибка сервера (код $code)"
    }
}

class HttpError(val code: Int, val body: String?) :
    IOException(httpErrorMessage(code, body))

fun Throwable.userMessage(): String = when (this) {
    is HttpError -> message ?: "Ошибка сервера (код $code)"
    is HttpException -> httpErrorMessage(
        code(),
        try {
            response()?.errorBody()?.string()
        } catch (e: Exception) {
            null
        }
    )
    is IOException -> "Сервер недоступен. Проверьте подключение к сети"
    else -> localizedMessage ?: "Неизвестная ошибка"
}


class AuthInterceptor(private val session: SessionStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val login = session.login
        val password = session.password
        if (!login.isNullOrEmpty() && !password.isNullOrEmpty()) {
            val credentials = okhttp3.Credentials.basic(login, password)
            builder.header("Authorization", credentials)
        }
        return chain.proceed(builder.build())
    }
}


object ApiClient {

    const val BASE_URL = "http://10.0.2.2:8080/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    fun create(session: SessionStore): CalorieApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(session))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CalorieApi::class.java)
    }
}
