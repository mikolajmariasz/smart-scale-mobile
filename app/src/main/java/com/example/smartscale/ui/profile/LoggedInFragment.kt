package com.example.smartscale.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartscale.R
import com.example.smartscale.databinding.FragmentLoggedInBinding
import java.io.File

class LoggedInFragment : Fragment() {

    private var _binding: FragmentLoggedInBinding? = null
    private val binding get() = _binding!!
    private val CAMERA_PERMISSION_CODE = 100

    private var imageUri: Uri? = null
    private var photoUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == android.app.Activity.RESULT_OK && photoUri != null) {
            binding.profileImage.setImageURI(photoUri)
        } else {
            Toast.makeText(context, "Nie udało się zrobić zdjęcia", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.profileImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoggedInBinding.inflate(inflater, container, false)

        val user = AuthRepository.getCurrentUser()

        binding.welcomeText.text = user?.let { "Witaj, ${it.username}!" } ?: "Nie jesteś zalogowany"
        binding.clientIdText.text = user?.clientId?.let { "ID klienta: $it" } ?: "Brak ID klienta"
        binding.userEmailText.text = user?.email?.let { "Email: $it" } ?: "Błąd bazy danych"

        binding.profileImage.setOnClickListener {
            showImagePickerDialog()
        }

        binding.logoutButton.setOnClickListener {
            AuthRepository.logout()
            Toast.makeText(context, "Wylogowano", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loggedInFragment_to_profileFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lifestyleOptions = listOf("Nieruchliwy", "Średnio aktywny", "Aktywny")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lifestyleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.lifestyleSpinner.adapter = adapter

        binding.calculateButton.setOnClickListener {
            calculateBmiAndCalories()
        }
    }

    private fun calculateBmiAndCalories() {
        val age = binding.ageInput.text.toString().toIntOrNull()
        val heightCm = binding.heightInput.text.toString().toIntOrNull()
        val weightKg = binding.weightInput.text.toString().toFloatOrNull()

        if (age == null || heightCm == null || weightKg == null) {
            Toast.makeText(context, "Wypełnij poprawnie wszystkie pola", Toast.LENGTH_SHORT).show()
            return
        }

        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        val lifestyleMultiplier = when (binding.lifestyleSpinner.selectedItem.toString()) {
            "Nieruchliwy" -> 1.2
            "Średnio aktywny" -> 1.55
            "Aktywny" -> 1.9
            else -> 1.2
        }

        val bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + 5
        val calories = bmr * lifestyleMultiplier

        binding.resultText.text = "BMI: %.2f\nZapotrzebowanie kaloryczne: %.0f kcal".format(bmi, calories)
    }

    private fun showImagePickerDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Zmień zdjęcie profilowe")
            .setItems(arrayOf("Zrób zdjęcie", "Wybierz z galerii")) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun checkCameraPermission() {
        val context = requireContext()
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val photoFile = File(requireContext().externalCacheDir, "temp_photo.jpg")
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        cameraLauncher.launch(cameraIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(context, "Brak zgody na użycie aparatu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
