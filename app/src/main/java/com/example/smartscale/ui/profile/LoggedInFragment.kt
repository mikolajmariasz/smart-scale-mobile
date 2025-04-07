package com.example.smartscale.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartscale.R
import com.example.smartscale.ui.profile.AuthRepository
import com.example.smartscale.databinding.FragmentLoggedInBinding

class LoggedInFragment : Fragment() {

    private var _binding: FragmentLoggedInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoggedInBinding.inflate(inflater, container, false)

        // Pobieramy aktualnie zalogowanego użytkownika
        val user = AuthRepository.getCurrentUser()

        // Wyświetlamy np. „Witaj, userX!” lub komunikat, jeśli nikt nie jest zalogowany
        binding.welcomeText.text = if (user != null) {
            "Witaj, ${user.username}!"
        } else {
            "Nie jesteś zalogowany"
        }

        // Obsługa kliknięcia przycisku „Wyloguj”
        binding.logoutButton.setOnClickListener {
            AuthRepository.logout()
            Toast.makeText(context, "Wylogowano", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loggedInFragment_to_profileFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
