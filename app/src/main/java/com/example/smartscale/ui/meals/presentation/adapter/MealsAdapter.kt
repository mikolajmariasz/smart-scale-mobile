package com.example.smartscale.ui.meals.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartscale.R
import com.example.smartscale.domain.model.Meal

class MealsAdapter(
    private var meals: List<Meal>,
    private val onAddMealClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TYPE_MEAL = 0
        const val TYPE_ADD_MEAL = 1
    }

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealTime: TextView = itemView.findViewById(R.id.mealTime)
        val mealEmoji: TextView = itemView.findViewById(R.id.meal_emoji)
        val mealCalories: TextView = itemView.findViewById(R.id.mealCalories)
        val mealCarbs: TextView = itemView.findViewById(R.id.mealCarbs)
        val mealProtein: TextView = itemView.findViewById(R.id.mealProtein)
        val mealFat: TextView = itemView.findViewById(R.id.mealFat)
    }

    class AddMealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addButton: ImageView = itemView.findViewById(R.id.addButton)
    }

    override fun getItemViewType(position: Int) =
        if (position < meals.size) TYPE_MEAL else TYPE_ADD_MEAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_MEAL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_meal, parent, false)
            MealViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add, parent, false)
            AddMealViewHolder(view)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MealViewHolder -> {
                val meal = meals[position]
                holder.mealName.text     = meal.name
                holder.mealTime.text     = meal.time
                holder.mealEmoji.text    = meal.emoji
                holder.mealCalories.text = String.format("%.1f", meal.calories)
                holder.mealCarbs.text    = String.format("%.1f", meal.carbs)
                holder.mealProtein.text  = String.format("%.1f", meal.protein)
                holder.mealFat.text      = String.format("%.1f", meal.fat)
            }
            is AddMealViewHolder -> {
                holder.addButton.setOnClickListener {
                    onAddMealClick()
                }
            }
        }
    }

    override fun getItemCount() = meals.size + 1

    fun updateMeals(newMeals: List<Meal>) {
        meals = newMeals.toList()
        notifyDataSetChanged()
    }
}
