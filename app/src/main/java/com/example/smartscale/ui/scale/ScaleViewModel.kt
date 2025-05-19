package com.example.smartscale.ui.scale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScaleViewModel : ViewModel() {

    //Tutaj trzeba dodać że po zalogowaniu tworzy id klienta i je tutaj przypisuje
    private val _clientId = MutableLiveData<String>().apply {
        value = "ID klienta: 12345678"
    }
    val clientId: LiveData<String> = _clientId

    private val _text = MutableLiveData<String>().apply {
        value = """
            🔌 Konfiguracja wagi – krok po kroku:

            1. Włącz wagę lub urządzenie, które tworzy sieć WiFi o nazwie: "ScaleParing".
            2. Na telefonie, wejdź w ustawienia WiFi i połącz się z siecią "ScaleParing".
            3. Wróć do tej aplikacji i kliknij w zakładkę "Scale".
            4. Potwierdź otwarcie przeglądarki – zostaniesz przekierowany do strony konfiguracji wagi.
            5. Na stronie wpisz dane swojej sieci WiFi oraz identyfikator klienta.
            6. Po zapisaniu danych urządzenie automatycznie połączy się z Twoją siecią WiFi.

            ℹ️ Uwaga: Telefon może pokazać komunikat „Brak internetu” – zignoruj to na czas konfiguracji.
        """.trimIndent()
    }
    val text: LiveData<String> = _text

    // W przyszłości: metoda do ustawiania ID po zalogowaniu
    fun updateClientId(id: String) {
        _clientId.value = "ID klienta: $id"
    }
}
