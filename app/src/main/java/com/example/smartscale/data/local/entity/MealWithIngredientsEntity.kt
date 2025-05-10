package com.example.smartscale.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithIngredientsEntity(
    @Embedded
    val meal: MealEntity,

    @Relation(
        parentColumn = "localId",
        entityColumn = "mealLocalId"
    )
    val ingredients: List<IngredientEntity>
)
