package com.example.abssolutions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class StoreProductAdapter(
    private var products: List<StoreProduct>,
    private val onProductClick: (StoreProduct) -> Unit
) : RecyclerView.Adapter<StoreProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewProductDescription: TextView = itemView.findViewById(R.id.textViewProductDescription)
        val textViewProductPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val buttonBuy: MaterialButton = itemView.findViewById(R.id.buttonBuy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.textViewProductName.text = product.name
        holder.textViewProductDescription.text = product.description
        holder.textViewProductPrice.text = "$${String.format("%.2f", product.price)}"
        
        // Load product image using Glide
        if (product.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_product_placeholder)
                .into(holder.imageViewProduct)
        } else {
            holder.imageViewProduct.setImageResource(R.drawable.ic_product_placeholder)
        }
        
        // Set buy button click listener
        holder.buttonBuy.setOnClickListener {
            onProductClick(product)
        }
        
        // Set item click listener
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<StoreProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun filterProducts(query: String) {
        val filteredList = if (query.isEmpty()) {
            products
        } else {
            products.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true) ||
                product.category.contains(query, ignoreCase = true)
            }
        }
        updateProducts(filteredList)
    }
} 