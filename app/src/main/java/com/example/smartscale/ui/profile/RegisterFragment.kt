package com.example.smartscale.ui.profile

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartscale.R
import com.example.smartscale.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        AuthRepository.init(requireContext().applicationContext)
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val passwordConfirm = binding.passwordConfirmInput.text.toString().trim()

            if (username.isBlank() || email.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
                Toast.makeText(context, "Wszystkie pola są wymagane", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Nieprawidłowy adres e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(context, "Hasła nie są takie same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = AuthRepository.register(username, email, password)
            if (success) {
                Toast.makeText(context, "Zarejestrowano pomyślnie", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_profileFragment)
            } else {
                Toast.makeText(context, "Nazwa użytkownika już istnieje", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_profileFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
