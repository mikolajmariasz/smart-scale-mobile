package com.example.smartscale.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["localId"],
            childColumns  = ["mealLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealLocalId")]
)
data class IngredientEntity(
    @PrimaryKey
    val localId: String = UUID.randomUUID().toString(),
    val remoteId: String? = null,
    val mealLocalId: String,
    val name: String,
    val barcode: String,
    val weight: Float,
    val caloriesPer100g: Float,
    val carbsPer100g: Float,
    val proteinPer100g: Float,
    val fatPer100g: Float,
    val syncStatus: SyncStatus = SyncStatus.TO_SYNC
)
