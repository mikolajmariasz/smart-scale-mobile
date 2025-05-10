package com.example.smartscale.domain.model

data class Meal(
    val name: String,
    val time: String,
    val emoji: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fat: Float
)
