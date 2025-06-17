package com.example.smartscale.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val name: String,
    val weight: Float,
    val barcode: String,
    val caloriesPer100g: Float,
    val carbsPer100g: Float,
    val proteinPer100g: Float,
    val fatPer100g: Float
) : Parcelable {
    val calories: Float get() = weight * caloriesPer100g / 100
    val carbs: Float get() = weight * carbsPer100g / 100
    val protein: Float get() = weight * proteinPer100g / 100
    val fat: Float get() = weight * fatPer100g / 100
}