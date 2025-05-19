package com.example.smartscale.ui.meals.addMeal.view

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartscale.R
import com.example.smartscale.data.remote.model.Product
import com.example.smartscale.databinding.FragmentAddMealBinding
import com.example.smartscale.domain.model.Ingredient
import com.example.smartscale.ui.meals.addMeal.adapter.IngredientsAdapter
import com.example.smartscale.core.dialog.FoodEmojiPickerDialog
import com.example.smartscale.ui.meals.addMeal.viewModel.AddMealViewModel
import com.example.smartscale.core.utils.DateTimePicker
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

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Ingredient>("newIngredient")
            ?.observe(viewLifecycleOwner) { newIng ->
                ingredients.add(newIng)
                ingredientsAdapter.updateList(ingredients)
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Ingredient>("newIngredient")
            }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Product>("selectedProduct")
            ?.observe(viewLifecycleOwner) { prod ->
                val w = findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<Float>("selectedWeight") ?: 0f
                val fromApi = Ingredient(
                    name  = prod.productName.orEmpty(),
                    weight = w,
                    caloriesPer100g = prod.nutriments?.energyKcal100g ?: 0f,
                    carbsPer100g = prod.nutriments?.carbohydrates100g ?: 0f,
                    proteinPer100g = prod.nutriments?.proteins100g ?: 0f,
                    fatPer100g = prod.nutriments?.fat100g ?: 0f
                )
                ingredients.add(fromApi)
                ingredientsAdapter.updateList(ingredients)
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Product>("selectedProduct")
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Float>("selectedWeight")
            }

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
            findNavController().navigate(R.id.action_addMeal_to_searchProduct)
        }
        binding.ingredientsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }
    }

    private fun saveMeal() {
        val name      = binding.editMealName.text.toString().ifBlank { "Meal" }
        val timeStamp = calendar.timeInMillis
        val emoji     = binding.emojiPicker.text.toString()
        viewModel.addMeal(name, timeStamp, emoji, ingredients) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
