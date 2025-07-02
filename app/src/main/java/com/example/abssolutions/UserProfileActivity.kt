package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

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

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
    }

    private fun displayUserProfile(email: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("members")
        dbRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val member = snapshot.children.first()
                        val memberData = member.value as? Map<*, *>
                        val firstName = memberData?.get("firstName") as? String ?: ""
                        val lastName = memberData?.get("lastName") as? String ?: ""
                        val userType = memberData?.get("type") as? String ?: ""
                        val height = memberData?.get("height") as? String ?: ""
                        val weight = memberData?.get("weight") as? String ?: ""
                        val bmi = memberData?.get("bmi") as? String ?: ""
                        val bodyType = memberData?.get("bodyType") as? String ?: ""
                        val address = memberData?.get("address") as? String ?: ""
                        val contact = memberData?.get("contact") as? String ?: ""
                        val emailValue = memberData?.get("email") as? String ?: email
                        val profileImageUrl = memberData?.get("profileImageUrl") as? String ?: ""
                        // Set values to views
                        textViewProfileName.text = "$firstName $lastName"
                        textViewUserType.text = userType
                        textViewHeight.text = height
                        textViewWeight.text = weight
                        textViewBMIRate.text = bmi
                        textViewBodyType.text = bodyType
                        textViewEmail.text = emailValue
                        textViewMobileNo.text = contact
                        textViewAddress.text = address
                        // Load profile image using Glide
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this@UserProfileActivity)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_user_placeholder)
                                .error(R.drawable.ic_user_placeholder)
                                .into(imageViewProfile)
                        } else {
                            imageViewProfile.setImageResource(R.drawable.ic_user_placeholder)
                        }
                    } else {
                        Toast.makeText(this@UserProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserProfileActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
} 