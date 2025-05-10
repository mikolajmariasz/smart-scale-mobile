package com.example.smartscale.ui.meals.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartscale.R
import com.example.smartscale.databinding.FragmentMealsBinding
import com.example.smartscale.ui.common.decorations.FadeEdgeDecoration
import com.example.smartscale.ui.meals.MealsViewModel
import com.example.smartscale.ui.meals.presentation.adapter.MealsAdapter
import com.example.smartscale.ui.meals.presentation.view.CircularProgressBar
import com.example.smartscale.ui.meals.presentation.view.NutritionBarView
import java.text.SimpleDateFormat
import java.util.*

class MealsFragment : Fragment() {

    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MealsViewModel by viewModels()

    private lateinit var mealsAdapter: MealsAdapter
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var nutritionProgressBar: NutritionBarView

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupHeader()
        setupRecyclerView()
        setupProgressBars()
        observeMeals()
    }

    private fun setupHeader() {
        val today = Calendar.getInstance().time
        binding.headerDateCalendar.dateTextView.text = dateFormat.format(today)
        viewModel.setDate(today)

        binding.headerDateCalendar.calendarButton.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    cal.set(year, month, day)
                    val selected = cal.time
                    binding.headerDateCalendar.dateTextView.text = dateFormat.format(selected)
                    viewModel.setDate(selected)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupRecyclerView() {
        mealsAdapter = MealsAdapter(emptyList()) {
            findNavController().navigate(R.id.action_meals_to_add_meal)
        }
        binding.mealsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mealsAdapter
            addItemDecoration(FadeEdgeDecoration(requireContext(), 50))
        }
    }

    private fun setupProgressBars() {
        circularProgressBar  = binding.circularProgressBar
        nutritionProgressBar = binding.nutritionBarsView
    }

    private fun observeMeals() {
        viewModel.meals.observe(viewLifecycleOwner) { list ->
            mealsAdapter.updateMeals(list)
            val totalCal   = list.sumOf { it.calories.toDouble() }.toFloat()
            val totalCarbs = list.sumOf { it.carbs.toDouble() }.toFloat()
            val totalProt  = list.sumOf { it.protein.toDouble() }.toFloat()
            val totalFat   = list.sumOf { it.fat.toDouble() }.toFloat()

            val goalCal = 2800f
            circularProgressBar.setCalories(totalCal, goalCal)

            nutritionProgressBar.apply {
                setCarbsProgress(totalCarbs, 117f)
                setProteinProgress(totalProt, 60f)
                setFatProgress(totalFat, 256f)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
