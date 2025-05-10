package com.example.smartscale.ui.meals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartscale.data.local.AppDatabase
import com.example.smartscale.data.local.MealsLocalRepository
import com.example.smartscale.domain.model.Ingredient
import kotlinx.coroutines.launch

class AddMealViewModel(app: Application): AndroidViewModel(app) {
    private val repo = MealsLocalRepository(
        AppDatabase.getInstance(app).mealDao(),
        AppDatabase.getInstance(app).ingredientDao()
    )

    fun addMeal(
        name: String,
        dateTime: Long,
        emoji: String,
        ingredients: List<Ingredient>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repo.addMeal(name, dateTime, emoji, ingredients)
            onComplete()
        }
    }
}
