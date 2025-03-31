package com.example.smartscale.ui.meals.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartscale.R
import com.example.smartscale.ui.meals.domain.model.Meal

class MealsAdapter(private val meals: List<Meal>) : RecyclerView.Adapter<MealsAdapter.MealViewHolder>() {

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealTime: TextView = itemView.findViewById(R.id.mealTime)
        val mealEmoji: TextView = itemView.findViewById(R.id.meal_emoji)
        val mealCalories: TextView = itemView.findViewById(R.id.mealCalories)
        val mealCarbs: TextView = itemView.findViewById(R.id.mealCarbs)
        val mealProtein: TextView = itemView.findViewById(R.id.mealProtein)
        val mealFat: TextView = itemView.findViewById(R.id.mealFat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.mealName.text = meal.name
        holder.mealTime.text = meal.time
        holder.mealEmoji.text = meal.emoji
        holder.mealCalories.text = meal.calories.toString()
        holder.mealCarbs.text = "${meal.carbs}g"
        holder.mealProtein.text = "${meal.protein}g"
        holder.mealFat.text = "${meal.fat}g"
    }

    override fun getItemCount() = meals.size
}