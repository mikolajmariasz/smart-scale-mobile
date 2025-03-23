package com.example.smartscale.ui.meals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartscale.databinding.FragmentMealsBinding
import com.example.smartscale.ui.meals.view.CircularProgressBar
import com.example.smartscale.ui.meals.view.NutritionBarView

class MealsFragment : Fragment() {

    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var nutritrionProgressBar: NutritionBarView
    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mealsViewModel =
            ViewModelProvider(this).get(MealsViewModel::class.java)

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


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}