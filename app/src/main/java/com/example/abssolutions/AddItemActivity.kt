package com.example.abssolutions

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AddItemActivity : AppCompatActivity() {

    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemImageUrlEditText: EditText
    private lateinit var saveItemButton: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        itemNameEditText = findViewById(R.id.itemNameEditText)
        itemPriceEditText = findViewById(R.id.itemPriceEditText)
        itemImageUrlEditText = findViewById(R.id.itemImageUrlEditText)
        saveItemButton = findViewById(R.id.saveItemButton)

        db = FirebaseFirestore.getInstance()

        saveItemButton.setOnClickListener {
            saveNewItem()
        }
    }

    private fun saveNewItem() {
        val name = itemNameEditText.text.toString().trim()
        val price = itemPriceEditText.text.toString().trim()
        val imageUrl = itemImageUrlEditText.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Find the highest order number and add 1 for the new item
        db.collection("items")
            .orderBy("order", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                var newOrder = 0
                if (!documents.isEmpty) {
                    val highestOrder = documents.documents[0].getLong("order")
                    if (highestOrder != null) {
                        newOrder = highestOrder.toInt() + 1
                    }
                }

                val newItem = hashMapOf(
                    "name" to name,
                    "price" to price,
                    "imageUrl" to imageUrl,
                    "createdAt" to System.currentTimeMillis(),
                    "order" to newOrder
                )

                db.collection("items")
                    .add(newItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error adding item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting order number: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 