package com.example.smartscale.ui.meals.addMeal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartscale.R
import com.example.smartscale.domain.model.Ingredient

class IngredientsAdapter(
    private var items: List<Ingredient>,
    private val onAddClick: () -> Unit,
    private val onRemoveClick: (Ingredient) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PRODUCT = 0
        private const val TYPE_ADD     = 1
    }

    inner class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name      : TextView    = v.findViewById(R.id.ingredientName)
        val weight    : TextView    = v.findViewById(R.id.ingredientWeight)
        val kcal      : TextView    = v.findViewById(R.id.ingredientCalories)
        val protein   : TextView    = v.findViewById(R.id.ingredientProtein)
        val fat       : TextView    = v.findViewById(R.id.ingredientFat)
        val carbs     : TextView    = v.findViewById(R.id.ingredientCarbs)
        val deleteBtn : ImageView   = v.findViewById(R.id.deleteButton)  // ← tu
    }

    inner class AddViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val addBtn: ImageView = v.findViewById(R.id.addButton)
    }

    override fun getItemViewType(position: Int) =
        if (position < items.size) TYPE_PRODUCT else TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PRODUCT) {
            ProductViewHolder(inflater.inflate(R.layout.item_ingredient, parent, false))
        } else {
            AddViewHolder(inflater.inflate(R.layout.item_add, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            val ing = items[position]
            holder.name.text    = ing.name
            holder.weight.text  = "${ing.weight} g"
            holder.kcal.text    = "${ing.calories} kcal"
            holder.protein.text = "${ing.protein} g"
            holder.fat.text     = "${ing.fat} g"
            holder.carbs.text   = "${ing.carbs} g"

            holder.deleteBtn.setOnClickListener {
                onRemoveClick(ing)
            }
        } else if (holder is AddViewHolder) {
            holder.addBtn.setOnClickListener { onAddClick() }
        }
    }

    override fun getItemCount() = items.size + 1

    fun updateList(newList: List<Ingredient>) {
        items = newList
        notifyDataSetChanged()
    }
}
