package com.example.smartscale.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartscale.data.local.entity.MealEntity
import com.example.smartscale.data.local.entity.MealWithIngredientsEntity
import com.example.smartscale.data.local.entity.SyncStatus

@Dao
interface MealDao {

    @Transaction
    @Query("""
    SELECT * FROM meals
     WHERE dateTime BETWEEN :start AND :end
     ORDER BY dateTime ASC
  """)
    fun getMealsWithIngredients(
        start: Long,
        end: Long
    ): LiveData<List<MealWithIngredientsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Transaction
    @Query("SELECT * FROM meals WHERE localId = :localId")
    suspend fun getMealWithIngredientsById(localId: String): MealWithIngredientsEntity?

    @Update
    suspend fun updateMeal(meal: MealEntity): Int

    @Query("DELETE FROM meals WHERE localId = :localId")
    suspend fun deleteMealByLocalId(localId: String)

    @Query("SELECT * FROM meals WHERE syncStatus = :status")
    suspend fun getMealsBySyncStatus(status: SyncStatus): List<MealEntity>
}

