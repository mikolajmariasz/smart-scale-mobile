package com.example.smartscale.ui.meals.presentation.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartscale.R
import com.example.smartscale.databinding.FragmentAddMealBinding
import com.example.smartscale.domain.model.Ingredient
import com.example.smartscale.ui.meals.presentation.adapter.IngredientsAdapter
import com.example.smartscale.ui.common.dialog.FoodEmojiPickerDialog
import com.example.smartscale.ui.meals.AddMealViewModel
import com.example.smartscale.ui.utils.DateTimePicker
import java.text.SimpleDateFormat
import java.util.*

class AddMealFragment : Fragment() {

    private var _binding: FragmentAddMealBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMealViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private val ingredients = mutableListOf<Ingredient>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupDateTimePicker()
        setupEmojiPicker()
        setupIngredientsList()
        binding.saveMealButton.setOnClickListener { saveMeal() }
    }

    private fun setupDateTimePicker() {
        binding.editMealDateTime.setOnClickListener {
            DateTimePicker.pickDateTime(requireContext(), calendar) {
                calendar.time = it.time
                val formatted = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
                    .format(it.time)
                binding.editMealDateTime.setText(formatted)
            }
        }
    }

    private fun setupEmojiPicker() {
        binding.emojiPicker.setOnClickListener {
            FoodEmojiPickerDialog { emoji ->
                binding.emojiPicker.text = emoji
            }.show(parentFragmentManager, "FoodEmojiPicker")
        }
    }

    private fun setupIngredientsList() {
        ingredientsAdapter = IngredientsAdapter(ingredients) {
            showAddIngredientDialog()
        }
        binding.ingredientsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }
    }

    private fun showAddIngredientDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_ingredient)
            .setView(dialogView)
            .setPositiveButton(R.string.add) { _, _ ->
                val name    = dialogView.findViewById<EditText>(R.id.inputName).text.toString()
                val weight  = dialogView.findViewById<EditText>(R.id.inputWeight)
                    .text.toString().toFloatOrNull() ?: 0f
                val cal100  = dialogView.findViewById<EditText>(R.id.inputCal100)
                    .text.toString().toFloatOrNull() ?: 0f
                val prot100 = dialogView.findViewById<EditText>(R.id.inputProt100)
                    .text.toString().toFloatOrNull() ?: 0f
                val fat100  = dialogView.findViewById<EditText>(R.id.inputFat100)
                    .text.toString().toFloatOrNull() ?: 0f
                val carbs100= dialogView.findViewById<EditText>(R.id.inputCarbs100)
                    .text.toString().toFloatOrNull() ?: 0f

                ingredients.add(
                    Ingredient(name, weight, cal100, carbs100, prot100, fat100)
                )
                ingredientsAdapter.updateList(ingredients)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun saveMeal() {
        val name = binding.editMealName.text.toString().ifBlank { "Meal" }
        val timeStamp = calendar.timeInMillis
        val emoji = binding.emojiPicker.text.toString()
        viewModel.addMeal(name, timeStamp, emoji, ingredients) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
