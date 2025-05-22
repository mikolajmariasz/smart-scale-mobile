package com.example.smartscale.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartscale.R
import com.example.smartscale.data.AuthRepository
import com.example.smartscale.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Oba pola są wymagane", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = AuthRepository.login(requireContext(), username, password)
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_profileFragment_to_loggedInFragment)
                        },
                        onFailure = { err ->
                            Toast.makeText(
                                context,
                                err.localizedMessage ?: "Błąd logowania",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_registerFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
