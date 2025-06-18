package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import com.example.abssolutions.data.UserContract
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.util.Base64

class UserProfileActivity : AppCompatActivity() {

    private lateinit var textViewProfileName: TextView
    private lateinit var textViewUserType: TextView
    private lateinit var textViewHeight: TextView
    private lateinit var textViewWeight: TextView
    private lateinit var textViewBMIRate: TextView
    private lateinit var textViewBodyType: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewMobileNo: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var buttonEditProfile: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imageViewProfile: ImageView

    private var userEmail: String? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        textViewProfileName = findViewById(R.id.textViewProfileName)
        textViewUserType = findViewById(R.id.textViewUserType)
        textViewHeight = findViewById(R.id.textViewHeight)
        textViewWeight = findViewById(R.id.textViewWeight)
        textViewBMIRate = findViewById(R.id.textViewBMIRate)
        textViewBodyType = findViewById(R.id.textViewBodyType)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewMobileNo = findViewById(R.id.textViewMobileNo)
        textViewAddress = findViewById(R.id.textViewAddress)
        buttonEditProfile = findViewById(R.id.buttonEditProfile)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        // Explicitly enable the edit profile button
        buttonEditProfile.isEnabled = true

        // Get user email from the intent
        userEmail = intent.getStringExtra("USER_EMAIL")

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Fetch and display user data
        val email = userEmail // Create a local immutable copy
        if (email != null) {
            displayUserProfile(email)
        } else {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            finish() // Close activity if email is missing
        }

        // Set OnClickListener for the Edit Profile button
        buttonEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        // Set listener for bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_profile -> {
                    // Stay on current activity or refresh
                    true
                }
                R.id.navigation_workout -> {
                    val intent = Intent(this, WorkoutActivity::class.java)
                    // Optionally pass user email or other data to WorkoutActivity
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    finish() // Finish current activity to prevent backstack growth
                    true
                }
                R.id.navigation_store -> {
                    val intent = Intent(this, StoreActivity::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    finish()
                    true
                }
                // Add cases for other navigation items
                else -> false
            }
        }

        // Set the selected item in the bottom navigation
        bottomNavigationView.selectedItemId = R.id.navigation_profile
    }

    private fun displayUserProfile(email: String) {
        val userKey = email.replace('.', ',')
        database.child("users").child(userKey).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userMap = snapshot.value as? Map<*, *>
                val name = userMap?.get("name") as? String ?: ""
                val userType = userMap?.get("user_type") as? String ?: ""
                val gender = userMap?.get("gender") as? String ?: ""
                val weight = (userMap?.get("weight") as? Number)?.toDouble() ?: 0.0
                val height = (userMap?.get("height") as? Number)?.toDouble() ?: 0.0
                val bmiRate = (userMap?.get("bmi_rate") as? Number)?.toDouble() ?: 0.0
                val address = userMap?.get("address") as? String ?: ""
                val contactNo = userMap?.get("contact_no") as? String ?: ""
                val base64 = userMap?.get("profile_image_base64") as? String
                textViewProfileName.text = name
                textViewUserType.text = userType
                textViewHeight.text = "${height} cm"
                textViewWeight.text = "${weight} KG"
                textViewBMIRate.text = bmiRate.toString()
                textViewBodyType.text = "Normal"
                textViewEmail.text = email
                textViewMobileNo.text = contactNo
                textViewAddress.text = address
                if (!base64.isNullOrEmpty()) {
                    try {
                        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageViewProfile.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        imageViewProfile.setImageResource(R.drawable.ic_user_placeholder)
                    }
                } else {
                    imageViewProfile.setImageResource(R.drawable.ic_user_placeholder)
                }
            } else {
                imageViewProfile.setImageResource(R.drawable.ic_user_placeholder)
                Toast.makeText(this@UserProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            imageViewProfile.setImageResource(R.drawable.ic_user_placeholder)
            Toast.makeText(this@UserProfileActivity, "Error accessing database", Toast.LENGTH_SHORT).show()
        }
    }
} 