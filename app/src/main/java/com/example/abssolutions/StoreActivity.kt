package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class StoreActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: StoreProductAdapter
    private var userEmail: String? = null
    private var allProducts: List<StoreProduct> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        adapter = StoreProductAdapter(emptyList()) { product ->
            // Handle product click
            showProductDetails(product)
        }
        
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
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE
                val products = mutableListOf<StoreProduct>()
                
                for (document in result) {
                    try {
                        val product = StoreProduct(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            price = document.getDouble("price") ?: 0.0,
                            imageUrl = document.getString("imageUrl") ?: "",
                            category = document.getString("category") ?: "",
                            inStock = document.getBoolean("inStock") ?: true,
                            rating = document.getDouble("rating")?.toFloat() ?: 0.0f,
                            reviewCount = document.getLong("reviewCount")?.toInt() ?: 0
                        )
                        products.add(product)
                    } catch (e: Exception) {
                        Log.e("StoreActivity", "Error parsing product: ${document.id}", e)
                    }
                }
                
                allProducts = products
                adapter.updateProducts(products)
                
                if (products.isEmpty()) {
                    // Add sample products if Firestore is empty
                    addSampleProductsToFirestore()
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.e("StoreActivity", "Error getting products", exception)
                Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show()
                
                // Load sample products for demo
                loadSampleProducts()
            }
    }

    private fun addSampleProductsToFirestore() {
        val db = FirebaseFirestore.getInstance()
        val sampleProducts = listOf(
            mapOf(
                "name" to "Premium Dumbbells Set",
                "description" to "Professional grade dumbbells for home gym. Perfect for strength training.",
                "price" to 89.99,
                "imageUrl" to "",
                "category" to "Equipment",
                "inStock" to true,
                "rating" to 4.5,
                "reviewCount" to 127
            ),
            mapOf(
                "name" to "Protein Powder - Whey Isolate",
                "description" to "High-quality whey protein isolate for muscle recovery and growth.",
                "price" to 49.99,
                "imageUrl" to "",
                "category" to "Supplements",
                "inStock" to true,
                "rating" to 4.8,
                "reviewCount" to 89
            ),
            mapOf(
                "name" to "Yoga Mat Premium",
                "description" to "Non-slip yoga mat with carrying strap. Perfect for yoga and pilates.",
                "price" to 29.99,
                "imageUrl" to "",
                "category" to "Equipment",
                "inStock" to true,
                "rating" to 4.6,
                "reviewCount" to 203
            ),
            mapOf(
                "name" to "Resistance Bands Set",
                "description" to "Complete set of resistance bands for full body workouts.",
                "price" to 19.99,
                "imageUrl" to "",
                "category" to "Equipment",
                "inStock" to true,
                "rating" to 4.4,
                "reviewCount" to 156
            ),
            mapOf(
                "name" to "BCAA Amino Acids",
                "description" to "Branched-chain amino acids for muscle recovery and endurance.",
                "price" to 34.99,
                "imageUrl" to "",
                "category" to "Supplements",
                "inStock" to true,
                "rating" to 4.7,
                "reviewCount" to 67
            ),
            mapOf(
                "name" to "Jump Rope Professional",
                "description" to "Professional jump rope for cardio and coordination training.",
                "price" to 24.99,
                "imageUrl" to "",
                "category" to "Equipment",
                "inStock" to true,
                "rating" to 4.3,
                "reviewCount" to 94
            )
        )

        for (product in sampleProducts) {
            db.collection("products").add(product)
        }
        
        // Reload products after adding samples
        loadProductsFromFirestore()
    }

    private fun loadSampleProducts() {
        val sampleProducts = listOf(
            StoreProduct(
                id = "1",
                name = "Premium Dumbbells Set",
                description = "Professional grade dumbbells for home gym. Perfect for strength training.",
                price = 89.99,
                category = "Equipment",
                rating = 4.5f,
                reviewCount = 127
            ),
            StoreProduct(
                id = "2",
                name = "Protein Powder - Whey Isolate",
                description = "High-quality whey protein isolate for muscle recovery and growth.",
                price = 49.99,
                category = "Supplements",
                rating = 4.8f,
                reviewCount = 89
            ),
            StoreProduct(
                id = "3",
                name = "Yoga Mat Premium",
                description = "Non-slip yoga mat with carrying strap. Perfect for yoga and pilates.",
                price = 29.99,
                category = "Equipment",
                rating = 4.6f,
                reviewCount = 203
            ),
            StoreProduct(
                id = "4",
                name = "Resistance Bands Set",
                description = "Complete set of resistance bands for full body workouts.",
                price = 19.99,
                category = "Equipment",
                rating = 4.4f,
                reviewCount = 156
            )
        )
        
        allProducts = sampleProducts
        adapter.updateProducts(sampleProducts)
    }

    private fun showProductDetails(product: StoreProduct) {
        Toast.makeText(
            this,
            "Selected: ${product.name} - $${String.format("%.2f", product.price)}",
            Toast.LENGTH_SHORT
        ).show()
        
        // Here you can implement a detailed product view or purchase flow
        // For now, just show a toast message
    }
} 