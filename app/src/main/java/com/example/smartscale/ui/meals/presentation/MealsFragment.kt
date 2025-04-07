package com.example.smartscale.ui.meals.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartscale.R
import com.example.smartscale.databinding.FragmentMealsBinding
import com.example.smartscale.decorations.FadeEdgeDecoration
import com.example.smartscale.ui.meals.domain.model.Meal
import com.example.smartscale.ui.meals.presentation.adapter.MealsAdapter
import com.example.smartscale.ui.meals.presentation.view.CircularProgressBar
import com.example.smartscale.ui.meals.presentation.view.NutritionBarView

class MealsFragment : Fragment() {

    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var nutritrionProgressBar: NutritionBarView
    private lateinit var mealsAdapter: MealsAdapter
    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Calories progress bar
        circularProgressBar = binding.circularProgressBar

        val caloriesGoal = 2800
        val caloriesConsumed = 2000
        val progress = caloriesConsumed.toFloat() / caloriesGoal.toFloat()

        circularProgressBar.setCaloriesText("$caloriesConsumed/$caloriesGoal kcal")
        circularProgressBar.setProgress(progress)

        // Nutrition progress bars
        nutritrionProgressBar = binding.nutritionBarsView

        nutritrionProgressBar.setCarbsProgress(11, 117)
        nutritrionProgressBar.setProteinProgress(20, 60)
        nutritrionProgressBar.setFatProgress(95, 256)

        // Setup RecyclerView
        setupRecyclerView()

        return root
    }

    private fun setupRecyclerView() {
        // Example
        val mealsList = listOf(
            Meal("Breakfast", "12.05 08:30", "🍳", 450, 50, 30, 20),
            Meal("Lunch", "12.05 13:15", "🍲", 650, 70, 40, 25),
            Meal("Dinner", "12.05 19:45", "🥗", 400, 30, 25, 15)
        )

        mealsAdapter = MealsAdapter(mealsList) {
            navigateToAddMeal()
        }
        binding.mealsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mealsAdapter
            addItemDecoration(FadeEdgeDecoration(requireContext(),50))
        }

    }

    private fun navigateToAddMeal() {
        val navController = findNavController()
        navController.navigate(R.id.action_meals_to_add_meal)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}