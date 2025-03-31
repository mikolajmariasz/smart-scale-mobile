package com.example.smartscale.ui.meals.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MealsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is meals Fragment"
    }
    val text: LiveData<String> = _text
}