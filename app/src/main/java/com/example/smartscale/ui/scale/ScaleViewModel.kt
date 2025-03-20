package com.example.smartscale.ui.scale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScaleViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is scale Fragment"
    }
    val text: LiveData<String> = _text
}