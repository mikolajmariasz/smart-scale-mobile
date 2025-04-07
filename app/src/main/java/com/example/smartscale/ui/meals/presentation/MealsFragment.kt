package com.example.smartscale.ui.meals.presentation

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class MealsFragment : Fragment() {

    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var nutritrionProgressBar: NutritionBarView
    private lateinit var mealsAdapter: MealsAdapter
    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupHeader()
        setupProgressBars()
        setupRecyclerView()

        return root
    }

    private fun setupHeader() {
        binding.headerDateCalendar.dateTextView.text = dateFormat.format(calendar.time)

        binding.headerDateCalendar.calendarButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.headerDateCalendar.dateTextView.text = dateFormat.format(calendar.time)
                loadMealsForSelectedDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadMealsForSelectedDate() {
        // TODO: Tutaj będzie ładowanie posiłków z bazy danych dla wybranej daty
        val selectedDate = dateFormat.format(calendar.time)
        val mealsList = listOf(
            Meal("Breakfast", "$selectedDate 08:30", "🍳", 450, 50, 30, 20),
            Meal("Lunch", "$selectedDate 13:15", "🍲", 650, 70, 40, 25),
            Meal("Dinner", "$selectedDate 19:45", "🥗", 400, 30, 25, 15)
        )
        mealsAdapter.updateMeals(mealsList)
        updateProgressBars()
    }

    private fun setupProgressBars() {
        circularProgressBar = binding.circularProgressBar
        nutritrionProgressBar = binding.nutritionBarsView
        updateProgressBars()
    }

    private fun updateProgressBars() {
        val caloriesGoal = 2800
        val caloriesConsumed = 2000
        val progress = caloriesConsumed.toFloat() / caloriesGoal.toFloat()

        circularProgressBar.setCaloriesText("$caloriesConsumed/$caloriesGoal kcal")
        circularProgressBar.setProgress(progress)

        nutritrionProgressBar.setCarbsProgress(11, 117)
        nutritrionProgressBar.setProteinProgress(20, 60)
        nutritrionProgressBar.setFatProgress(95, 256)
    }

    private fun setupRecyclerView() {
        mealsAdapter = MealsAdapter(emptyList()) {
            navigateToAddMeal()
        }
        binding.mealsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mealsAdapter
            addItemDecoration(FadeEdgeDecoration(requireContext(), 50))
        }

        loadMealsForSelectedDate()
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