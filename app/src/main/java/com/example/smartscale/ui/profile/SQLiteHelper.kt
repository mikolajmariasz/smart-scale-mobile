package com.example.smartscale.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.smartscale.ui.profile.User
class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, "smartscale.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("SQLiteHelper", "onCreate: Tworzenie tabeli users")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                email TEXT,
                password TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun registerUser(user: User): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", arrayOf(user.username))
        val exists = cursor.count > 0
        cursor.close()

        if (exists) return false

        val values = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("password", user.password)
        }

        val result = db.insert("users", null, values)
        return result != -1L
    }

    fun loginUser(usernameOrEmail: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?",
            arrayOf(usernameOrEmail, usernameOrEmail, password)
        )

        return if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val pass = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            cursor.close()
            User(username, email, pass)
        } else {
            cursor.close()
            null
        }
    }
}

