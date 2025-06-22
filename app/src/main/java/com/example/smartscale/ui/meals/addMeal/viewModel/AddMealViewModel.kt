package com.example.smartscale.ui.meals.addMeal.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartscale.data.local.AppDatabase
import com.example.smartscale.data.local.MealsLocalRepository
import com.example.smartscale.data.local.entity.IngredientEntity
import com.example.smartscale.data.local.entity.MealWithIngredientsEntity
import com.example.smartscale.domain.model.Ingredient
import kotlinx.coroutines.launch

class AddMealViewModel(app: Application): AndroidViewModel(app) {
    private val repo = MealsLocalRepository(
        AppDatabase.getInstance(app).mealDao(),
        AppDatabase.getInstance(app).ingredientDao()
    )

    private val _mealName = MutableLiveData<String>()
    val mealName: LiveData<String> = _mealName

    private val _mealDateTime = MutableLiveData<Long>()
    val mealDateTime: LiveData<Long> = _mealDateTime

    private val _mealEmoji = MutableLiveData<String>()
    val mealEmoji: LiveData<String> = _mealEmoji

    private val _ingredientsList = MutableLiveData<List<Ingredient>>(emptyList())
    val ingredientsList: LiveData<List<Ingredient>> = _ingredientsList

    private var editingLocalId: String? = null

    fun loadMeal(localId: String) {
        editingLocalId = localId
        viewModelScope.launch {
            val mw: MealWithIngredientsEntity? = repo.getMealForEdit(localId)
            mw?.let {
                _mealName.postValue(it.meal.name)
                _mealDateTime.postValue(it.meal.dateTime)
                _mealEmoji.postValue(it.meal.emoji)
                val list = it.ingredients.map { entity -> entity.toDomain() }
                _ingredientsList.postValue(list)
            }
        }
    }

    fun saveMeal(
        name: String,
        dateTime: Long,
        emoji: String,
        ingredients: List<Ingredient>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            if (editingLocalId != null) {
                // tryb edycji
                repo.updateMeal(
                    localId = editingLocalId!!,
                    name = name,
                    dateTime = dateTime,
                    emoji = emoji,
                    ingredients = ingredients
                )
            } else {
                // tryb dodawania
                repo.addMeal(name, dateTime, emoji, ingredients)
            }
            onComplete()
        }
    }

    fun deleteMeal(onComplete: () -> Unit) {
        editingLocalId?.let { id ->
            viewModelScope.launch {
                repo.deleteMeal(id)
                onComplete()
            }
        }
    }

    fun setIngredients(ings: List<Ingredient>) {
        _ingredientsList.value = ings
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
