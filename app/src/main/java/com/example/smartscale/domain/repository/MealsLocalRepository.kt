package com.example.smartscale.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.smartscale.data.local.dao.IngredientDao
import com.example.smartscale.data.local.dao.MealDao
import com.example.smartscale.data.local.entity.*
import com.example.smartscale.domain.model.Ingredient
import com.example.smartscale.domain.model.Meal
import java.text.SimpleDateFormat
import java.util.*

class MealsLocalRepository(
    private val mealDao: MealDao,
    private val ingredientDao: IngredientDao
) {

    private val sdf = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

    fun getMealsForPeriod(startTs: Long, endTs: Long): LiveData<List<Meal>> =
        mealDao.getMealsWithIngredients(startTs, endTs).map { list ->
            list.map { mw ->
                val entity = mw.meal
                val ingrList = mw.ingredients.map { it.toDomain() }
                val totalKcal = ingrList.sumOf { it.calories.toDouble() }.toFloat()
                val totalCarbs = ingrList.sumOf { it.carbs.toDouble() }.toFloat()
                val totalProt  = ingrList.sumOf { it.protein.toDouble() }.toFloat()
                val totalFat   = ingrList.sumOf { it.fat.toDouble() }.toFloat()
                val timeString = sdf.format(Date(entity.dateTime))

                Meal(
                    localId  = entity.localId,
                    dateTime = entity.dateTime,
                    name      = entity.name,
                    time      = timeString,
                    emoji     = entity.emoji,
                    calories  = totalKcal,
                    carbs     = totalCarbs,
                    protein   = totalProt,
                    fat       = totalFat
                )
            }
        }

    suspend fun addMeal(
        name: String,
        dateTime: Long,
        emoji: String,
        ingredients: List<Ingredient>
    ) {
        val mealEntity = MealEntity(
            name = name,
            dateTime = dateTime,
            emoji = emoji
        )
        mealDao.insertMeal(mealEntity)

        ingredients.forEach { ing ->
            ingredientDao.insertIngredient(
                IngredientEntity(
                    mealLocalId      = mealEntity.localId,
                    name             = ing.name,
                    weight           = ing.weight,
                    caloriesPer100g  = ing.caloriesPer100g,
                    carbsPer100g     = ing.carbsPer100g,
                    proteinPer100g   = ing.proteinPer100g,
                    fatPer100g       = ing.fatPer100g
                )
            )
        }
    }

    suspend fun getMealForEdit(localId: String): MealWithIngredientsEntity? =
        mealDao.getMealWithIngredientsById(localId)

    suspend fun updateMeal(
        localId: String,
        name: String,
        dateTime: Long,
        emoji: String,
        ingredients: List<Ingredient>
    ) {
        val mealEntity = MealEntity(
            localId   = localId,
            name      = name,
            dateTime  = dateTime,
            emoji     = emoji,
        )
        mealDao.updateMeal(mealEntity)

        ingredientDao.deleteIngredientsByMealLocalId(localId)

        ingredients.forEach { ing ->
            ingredientDao.insertIngredient(
                IngredientEntity(
                    mealLocalId      = localId,
                    name             = ing.name,
                    weight           = ing.weight,
                    caloriesPer100g  = ing.caloriesPer100g,
                    carbsPer100g     = ing.carbsPer100g,
                    proteinPer100g   = ing.proteinPer100g,
                    fatPer100g       = ing.fatPer100g
                )
            )
        }
    }

    suspend fun deleteMeal(localId: String) {
        ingredientDao.deleteIngredientsByMealLocalId(localId)
        mealDao.deleteMealByLocalId(localId)
    }

    suspend fun searchIngredients(query: String): List<Ingredient> {
        return ingredientDao
            .searchIngredientsByName(query)
            .map { it.toDomain() }
    }
}

private fun IngredientEntity.toDomain(): Ingredient =
    Ingredient(
        name            = name,
        weight          = weight,
        caloriesPer100g = caloriesPer100g,
        carbsPer100g    = carbsPer100g,
        proteinPer100g  = proteinPer100g,
        fatPer100g      = fatPer100g
    )
