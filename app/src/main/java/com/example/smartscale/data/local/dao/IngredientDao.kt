package com.example.smartscale.data.local.dao

import androidx.room.*
import com.example.smartscale.data.local.entity.IngredientEntity
import com.example.smartscale.data.local.entity.SyncStatus

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity): Int

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%'")
    suspend fun searchIngredientsByName(query: String): List<IngredientEntity>

    @Query("DELETE FROM ingredients WHERE mealLocalId = :mealLocalId")
    suspend fun deleteIngredientsByMealLocalId(mealLocalId: String)

    @Query("SELECT * FROM ingredients WHERE mealLocalId = :mealId")
    suspend fun getIngredientsForMeal(mealId: String): List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE syncStatus = :status")
    suspend fun getIngredientsBySyncStatus(status: SyncStatus): List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE barcode = :barcode LIMIT 1")
    suspend fun getIngredientByBarcode(barcode: String): IngredientEntity?


}

