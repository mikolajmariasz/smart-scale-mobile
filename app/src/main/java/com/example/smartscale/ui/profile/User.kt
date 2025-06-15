package com.example.smartscale.ui.profile

data class User(
    val username: String,
    val email: String,
    val password: String,
    val clientId: String = (100000..999999).random().toString()
)

