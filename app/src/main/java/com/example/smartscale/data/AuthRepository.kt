package com.example.smartscale.data

import android.content.Context
import com.example.smartscale.data.LoginRequest

data class User(val username: String)

object AuthRepository {
    private const val PREFS = "auth"
    private const val KEY_USERNAME = "username"

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<String> {
        return try {
            val resp = RetrofitClient.authService.register(
                RegisterRequest(username, email, password)
            )
            if (resp.isSuccessful) {
                val body = resp.body()
                if (resp.code() == 201 || body?.message != null) {
                    // zwracamy success i jednocześnie zapisujemy token
                    Result.success(body?.message ?: "Zarejestrowano")
                } else {
                    Result.failure(Exception(body?.message ?: "Nieznany błąd"))
                }
            } else {
                // np. 400 ze zwróconym JSON-em {"error":"Username already exists"}
                val errorMsg = resp.errorBody()?.string() ?: resp.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        context: Context,
        username: String,
        password: String
    ): Result<String> {
        return try {
            val resp = RetrofitClient.authService.login(LoginRequest(username, password))
            if (resp.isSuccessful) {
                val msg = resp.body()?.message ?: "Zalogowano"
                // Zapisz username jako „zalogowanego”
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_USERNAME, username)
                    .apply()
                Result.success(msg)
            } else {
                Result.failure(Exception(resp.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val username = prefs.getString(KEY_USERNAME, null)
        return username?.let { User(it) }
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_USERNAME)
            .apply()
    }
}
