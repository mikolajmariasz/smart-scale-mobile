// CustomProductDialogFragment.kt
package com.example.smartscale.ui.meals.searchProduct.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.fragment.app.DialogFragment
import com.example.smartscale.R
import com.example.smartscale.databinding.DialogAddIngredientBinding
import com.example.smartscale.domain.model.Ingredient

class CustomProductDialogFragment(
    private val onAdd: (Ingredient) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddIngredientBinding.inflate(LayoutInflater.from(requireContext()))

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_custom_product)
            .setView(binding.root)
            .setPositiveButton(R.string.add) { _, _ ->
                val name    = binding.inputName.text.toString().ifBlank { "Custom" }
                val weight  = binding.inputWeight.text.toString().toFloatOrNull() ?: 0f
                val cal100  = binding.inputCal100.text.toString().toFloatOrNull() ?: 0f
                val prot100 = binding.inputProt100.text.toString().toFloatOrNull() ?: 0f
                val fat100  = binding.inputFat100.text.toString().toFloatOrNull() ?: 0f
                val carbs100= binding.inputCarbs100.text.toString().toFloatOrNull() ?: 0f

                onAdd(
                    Ingredient(
                        name = name,
                        weight = weight,
                        caloriesPer100g = cal100,
                        carbsPer100g = carbs100,
                        proteinPer100g = prot100,
                        fatPer100g = fat100
                    )
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
