// CustomProductDialogFragment.kt
package com.example.smartscale.ui.meals.searchProduct.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
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

        binding.inputName.hint = getString(R.string.name)
        binding.inputWeight.hint = getString(R.string.weight_g)
        binding.inputWeight.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        binding.inputCal100.hint = getString(R.string.calories_per_100g)
        binding.inputProt100.hint = getString(R.string.protein_per_100g)
        binding.inputFat100.hint = getString(R.string.fat_per_100g)
        binding.inputCarbs100.hint = getString(R.string.carbs_per_100g)

        return AlertDialog.Builder(requireContext())
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
