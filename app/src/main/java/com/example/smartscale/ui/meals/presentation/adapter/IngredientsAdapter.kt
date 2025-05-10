package com.example.smartscale.ui.meals.presentation.adapter

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
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PRODUCT = 0
        private const val TYPE_ADD     = 1
    }

    inner class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name      : TextView = v.findViewById(R.id.ingredientName)
        val weight    : TextView = v.findViewById(R.id.ingredientWeight)
        val kcal      : TextView = v.findViewById(R.id.ingredientCalories)
        val protein   : TextView = v.findViewById(R.id.ingredientProtein)
        val fat       : TextView = v.findViewById(R.id.ingredientFat)
        val carbs     : TextView = v.findViewById(R.id.ingredientCarbs)
    }

    inner class AddViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val addBtn: ImageView = v.findViewById(R.id.addButton)
    }

    override fun getItemViewType(position: Int) =
        if (position < items.size) TYPE_PRODUCT else TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PRODUCT) {
            val v = inflater.inflate(R.layout.item_ingredient, parent, false)
            ProductViewHolder(v)
        } else {
            val v = inflater.inflate(R.layout.item_add, parent, false)
            AddViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            val p = items[position]
            holder.name.text    = p.name
            holder.weight.text  = "${p.weight} g"
            holder.kcal.text    = "${p.calories} kcal"
            holder.protein.text = "${p.protein} g"
            holder.fat.text     = "${p.fat} g"
            holder.carbs.text   = "${p.carbs} g"
        } else if (holder is AddViewHolder) {
            holder.addBtn.setOnClickListener { onAddClick() }
        }
    }

    override fun getItemCount() = items.size + 1

    fun updateList(newList: List<Ingredient>) {
        items = newList.toList()
        notifyDataSetChanged()
    }
}