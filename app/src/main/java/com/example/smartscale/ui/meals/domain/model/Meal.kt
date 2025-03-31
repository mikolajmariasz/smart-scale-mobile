package com.example.smartscale.ui.meals.domain.model

data class Meal(
    val name: String,
    val time: String,
    val emoji: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)