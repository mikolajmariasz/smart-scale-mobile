package com.example.smartscale.ui.meals.searchProduct.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartscale.R
import com.example.smartscale.data.remote.model.Product

class ProductsAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private var showLoading: Boolean = false

    override fun getItemCount(): Int = products.size + if (showLoading) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (position < products.size) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            ProductViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
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
    }

    fun setLoadingEnabled(enabled: Boolean) {
        if (enabled == showLoading) return
        showLoading = enabled
        if (enabled) notifyItemInserted(products.size)
        else notifyItemRemoved(products.size)
    }


    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageIv: ImageView = itemView.findViewById(R.id.productImage)
        val nameTv: TextView = itemView.findViewById(R.id.productName)
        val kcalTv: TextView = itemView.findViewById(R.id.productKcal)
        val proteinTv: TextView = itemView.findViewById(R.id.productProtein)
        val carbsTv: TextView = itemView.findViewById(R.id.productCarbs)
        val fatTv: TextView = itemView.findViewById(R.id.productFat)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.footerProgressBar)
    }
}
