package com.example.smartscale.ui.meals.mealList.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.smartscale.data.local.AppDatabase
import com.example.smartscale.data.local.MealsLocalRepository
import com.example.smartscale.domain.model.Meal
import java.util.*

class MealsViewModel(app: Application): AndroidViewModel(app) {
    private val repo = MealsLocalRepository(
        AppDatabase.getInstance(app).mealDao(),
        AppDatabase.getInstance(app).ingredientDao()
    )

    private val _selectedDate = MutableLiveData<Date>(Date())
    val meals: LiveData<List<Meal>> = _selectedDate.switchMap { date ->
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        repo.getMealsForPeriod(start, end)
    }

    fun setDate(date: Date) {
        _selectedDate.value = date
    }
}
