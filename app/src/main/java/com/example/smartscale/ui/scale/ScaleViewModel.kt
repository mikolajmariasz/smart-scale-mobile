package com.example.smartscale.ui.scale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScaleViewModel : ViewModel() {

    private val _clientIdInput = MutableLiveData<String>()
    private val _wifiNameInput = MutableLiveData<String>()
    private val _wifiPasswordInput = MutableLiveData<String>()
    private val _qrContent = MutableLiveData<String>()

    fun updateClientIdInput(id: String) {
        _clientIdInput.value = id
    }

    fun updateWifiNameInput(name: String) {
        _wifiNameInput.value = name
    }

    fun updateWifiPasswordInput(password: String) {
        _wifiPasswordInput.value = password
    }

    fun generateQrContent(): String? {
        val id = _clientIdInput.value
        val ssid = _wifiNameInput.value
        val password = _wifiPasswordInput.value

        return if (!id.isNullOrBlank() && !ssid.isNullOrBlank() && !password.isNullOrBlank()) {
            val content = "&&$id;$ssid;$password"
            _qrContent.value = content
            content
        } else {
            null
        }
    }
}
