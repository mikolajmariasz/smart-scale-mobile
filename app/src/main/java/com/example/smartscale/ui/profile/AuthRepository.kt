package com.example.smartscale.ui.profile

object AuthRepository {
    private val registeredUsers = mutableListOf<User>()
    private var currentUser: User? = null

    fun register(username: String, email: String, password: String): Boolean {
        if (registeredUsers.any { it.username == username }) return false
        registeredUsers.add(User(username, email, password))
        return true
    }

    fun login(username: String, password: String): Boolean {
        val user = registeredUsers.find {
            (it.username == username || it.email == username) && it.password == password
        }
        return if (user != null) {
            currentUser = user
            true
        } else {
            false
        }
    }

    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}

