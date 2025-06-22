package com.example.smartscale.ui.meals.addMeal.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartscale.R
import com.example.smartscale.databinding.FragmentAddMealBinding
import com.example.smartscale.domain.model.Ingredient
import com.example.smartscale.ui.meals.addMeal.adapter.IngredientsAdapter
import com.example.smartscale.core.dialog.FoodEmojiPickerDialog
import com.example.smartscale.ui.meals.addMeal.viewModel.AddMealViewModel
import com.example.smartscale.core.utils.DateTimePicker
import java.text.SimpleDateFormat
import java.util.*

class AddMealFragment : Fragment() {
    private val args: AddMealFragmentArgs by navArgs()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.mealId.isNotEmpty()) {
            viewModel.loadMeal(args.mealId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupDateTimePicker()
        setupEmojiPicker()
        setupIngredientsList()

        if (args.mealId.isNotEmpty()) {
            binding.addMealTitle.text = getString(R.string.edit_meal)
            binding.saveMealButton.text = getString(R.string.update_meal)

            binding.deleteMealButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setPositiveButton(R.string.delete) { _, _ ->
                            viewModel.deleteMeal {
                                findNavController().popBackStack()
                            }
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }
        } else {
            binding.deleteMealButton.visibility = View.GONE
        }

        viewModel.mealName.observe(viewLifecycleOwner) { name ->
            binding.editMealName.setText(name)
        }
        viewModel.mealDateTime.observe(viewLifecycleOwner) { ts ->
            calendar.timeInMillis = ts
            val fmt = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
            binding.editMealDateTime.setText(fmt.format(Date(ts)))
        }
        viewModel.mealEmoji.observe(viewLifecycleOwner) { emoji ->
            binding.emojiPicker.text = emoji
        }
        viewModel.ingredientsList.observe(viewLifecycleOwner) { list ->
            ingredients.clear()
            ingredients.addAll(list)
            ingredientsAdapter.updateList(ingredients)
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Ingredient>("newIngredient")
            ?.observe(viewLifecycleOwner) { newIng ->
                val current = viewModel.ingredientsList.value.orEmpty()
                viewModel.setIngredients(current + newIng)
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Ingredient>("newIngredient")
            }

        binding.saveMealButton.setOnClickListener {
            val name      = binding.editMealName.text.toString().ifBlank { "Meal" }
            val timeStamp = calendar.timeInMillis
            val emoji     = binding.emojiPicker.text.toString()
            viewModel.saveMeal(name, timeStamp, emoji, viewModel.ingredientsList.value.orEmpty()) {
                findNavController().popBackStack()
            }
        }
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
        ingredientsAdapter = IngredientsAdapter(
            items = ingredients,
            onAddClick = {
                val action = AddMealFragmentDirections
                    .actionAddMealToSearchProduct()
                findNavController().navigate(action)
            },
            onRemoveClick = { ing ->
                val current = viewModel.ingredientsList.value.orEmpty()
                viewModel.setIngredients(current - ing)
            }
        )
        binding.ingredientsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
