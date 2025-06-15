package com.example.smartscale.ui.profile

import android.content.Context
import com.example.smartscale.data.SQLiteHelper

object AuthRepository {
    private lateinit var dbHelper: SQLiteHelper
    private var currentUser: User? = null

    fun init(context: Context) {
        dbHelper = SQLiteHelper(context.applicationContext)
        dbHelper.writableDatabase
    }

    fun register(username: String, email: String, password: String): Boolean {
        val user = User(username, email, password)
        return dbHelper.registerUser(user)
    }

    fun login(usernameOrEmail: String, password: String): Boolean {
        val user = dbHelper.loginUser(usernameOrEmail, password)
        currentUser = user
        return user != null
    }

    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? = currentUser
}
