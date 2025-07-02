package com.example.abssolutions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class ProductViewActivity : AppCompatActivity() {

    private lateinit var productImageView: ImageView
    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var decreaseQuantityButton: Button
    private lateinit var increaseQuantityButton: Button
    private lateinit var buyNowButton: Button
    private lateinit var recommendedItemsRecyclerView: RecyclerView

    private var quantity = 1
    private var currentProduct: StoreProduct? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_view)

        productImageView = findViewById(R.id.productImageView)
        productNameTextView = findViewById(R.id.productNameTextView)
        productPriceTextView = findViewById(R.id.productPriceTextView)
        quantityTextView = findViewById(R.id.quantityTextView)
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton)
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton)
        buyNowButton = findViewById(R.id.buyNowButton)
        recommendedItemsRecyclerView = findViewById(R.id.recommendedItemsRecyclerView)

        // Get product from intent
        val productId = intent.getStringExtra("PRODUCT_ID")
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productImageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL")

        if (productId != null && productName != null && productPrice != null && productImageUrl != null) {
            currentProduct = StoreProduct(id = productId, name = productName, price = productPrice, imageUrl = productImageUrl)
            
            productNameTextView.text = productName
            productPriceTextView.text = "LKR $productPrice"
            Glide.with(this).load(productImageUrl).placeholder(R.drawable.ic_product_placeholder).into(productImageView)

            setupQuantityButtons()
            setupBuyNowButton()
            fetchRecommendedItems(productId)
        } else {
            Toast.makeText(this, "Error: Product details not found.", Toast.LENGTH_SHORT).show()
            finish()
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
    }

    private fun setupQuantityButtons() {
        decreaseQuantityButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityTextView.text = quantity.toString()
            }
        }
        increaseQuantityButton.setOnClickListener {
            quantity++
            quantityTextView.text = quantity.toString()
        }
    }

    private fun setupBuyNowButton() {
        buyNowButton.setOnClickListener {
            // Placeholder for buy now logic
            Toast.makeText(this, "Buying ${quantity} of ${currentProduct?.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRecommendedItems(currentProductId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("items")
            .get()
            .addOnSuccessListener { result ->
                val allProducts = result.documents.mapNotNull { document ->
                    StoreProduct(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        price = document.getString("price") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        createdAt = document.getLong("createdAt") ?: 0,
                        order = document.getLong("order")?.toInt() ?: 0
                    )
                }
                
                val recommendedProducts = allProducts
                    .filter { it.id != currentProductId }
                    .shuffled()
                    .take(10)

                setupRecommendedItemsRecyclerView(recommendedProducts)
            }
            .addOnFailureListener { exception ->
                Log.e("ProductViewActivity", "Error getting recommended items", exception)
            }
    }

    private fun setupRecommendedItemsRecyclerView(products: List<StoreProduct>) {
        val adapter = StoreProductAdapter(products, products,
            { product ->
                // Click on a recommended item could open its own view page
                 val intent = Intent(this, ProductViewActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                    putExtra("PRODUCT_NAME", product.name)
                    putExtra("PRODUCT_PRICE", product.price)
                    putExtra("PRODUCT_IMAGE_URL", product.imageUrl)
                }
                startActivity(intent)
            },
            { product ->
                // "Buy Now" on a recommended item
                Toast.makeText(this, "Buying ${product.name}", Toast.LENGTH_SHORT).show()
            }
        )
        recommendedItemsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendedItemsRecyclerView.adapter = adapter
    }
} 