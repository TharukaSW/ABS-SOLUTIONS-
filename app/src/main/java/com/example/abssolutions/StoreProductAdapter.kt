package com.example.abssolutions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class StoreProductAdapter(
    private var products: List<StoreProduct>,
    private var allProducts: List<StoreProduct>,
    private val onProductClick: (StoreProduct) -> Unit,
    private val onBuyClick: (StoreProduct) -> Unit
) : RecyclerView.Adapter<StoreProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
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
        holder.textViewProductPrice.text = "LKR ${product.price}"
        
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
        
        // Set item click listener
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        holder.buttonBuy.setOnClickListener {
            onBuyClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<StoreProduct>) {
        products = newProducts
        allProducts = newProducts
        notifyDataSetChanged()
    }

    fun filterProducts(query: String) {
        val filteredList = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true)
            }
        }
        products = filteredList
        notifyDataSetChanged()
    }
} 