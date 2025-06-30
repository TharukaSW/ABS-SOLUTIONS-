package com.example.abssolutions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StoreActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: StoreProductAdapter
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.store_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts)
        searchEditText = findViewById(R.id.searchEditText)
        progressBar = findViewById(R.id.progressBar)

        userEmail = intent.getStringExtra("USER_EMAIL")

        setupBottomNavigation()
        setupRecyclerView()
        setupSearch()
        loadProductsFromFirestore()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_profile -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_workout -> {
                    val intent = Intent(this, WorkoutActivity::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_store -> {
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.navigation_store
    }

    private fun setupRecyclerView() {
        adapter = StoreProductAdapter(emptyList(), emptyList(), 
            { product ->
                // Handle product click if needed
            },
            { product ->
                // Handle buy now click
                val intent = Intent(this, ProductViewActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                    putExtra("PRODUCT_NAME", product.name)
                    putExtra("PRODUCT_PRICE", product.price)
                    putExtra("PRODUCT_IMAGE_URL", product.imageUrl)
                }
                startActivity(intent)
            }
        )
        
        recyclerViewProducts.layoutManager = GridLayoutManager(this, 2)
        recyclerViewProducts.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                adapter.filterProducts(s?.toString() ?: "")
            }
        })
    }

    private fun loadProductsFromFirestore() {
        progressBar.visibility = View.VISIBLE
        
        val db = FirebaseFirestore.getInstance()
        db.collection("items")
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE
                val products = mutableListOf<StoreProduct>()
                
                for (document in result) {
                    try {
                        val product = StoreProduct(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            price = document.getString("price") ?: "",
                            imageUrl = document.getString("imageUrl") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0,
                            order = document.getLong("order")?.toInt() ?: 0
                        )
                        products.add(product)
                    } catch (e: Exception) {
                        Log.e("StoreActivity", "Error parsing product: ${document.id}", e)
                    }
                }
                
                adapter.updateProducts(products)
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.e("StoreActivity", "Error getting products", exception)
                Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadProductsFromFirestore()
    }
} 