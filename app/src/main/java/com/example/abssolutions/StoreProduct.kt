package com.example.abssolutions

data class StoreProduct(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val inStock: Boolean = true,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
) 