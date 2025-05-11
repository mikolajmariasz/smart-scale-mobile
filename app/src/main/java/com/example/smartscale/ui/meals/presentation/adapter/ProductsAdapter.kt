package com.example.smartscale.ui.meals.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartscale.R
import com.example.smartscale.data.remote.model.Product

class ProductsAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageIv: ImageView         = itemView.findViewById(R.id.productImage)
        val nameTv: TextView           = itemView.findViewById(R.id.productName)
        val kcalTv: TextView           = itemView.findViewById(R.id.productKcal)
        val proteinTv: TextView        = itemView.findViewById(R.id.productProtein)
        val carbsTv: TextView          = itemView.findViewById(R.id.productCarbs)
        val fatTv: TextView            = itemView.findViewById(R.id.productFat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val p = products[position]
        holder.nameTv.text = p.productName.orEmpty()
        Glide.with(holder.imageIv.context)
            .load(p.imageUrl)
            .placeholder(R.drawable.ic_meals_black_24dp)
            .into(holder.imageIv)

        val n = p.nutriments
        holder.kcalTv.text = n?.energyKcal100g?.let { "${it.toInt()} kcal" } ?: "- kcal"
        holder.proteinTv.text = n?.proteins100g?.let { "${it} g" } ?: "- g"
        holder.carbsTv.text = n?.carbohydrates100g?.let { "${it} g" } ?: "- g"
        holder.fatTv.text = n?.fat100g?.let { "${it} g" } ?: "- g"

        holder.itemView.setOnClickListener { onItemClick(p) }
    }

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}
