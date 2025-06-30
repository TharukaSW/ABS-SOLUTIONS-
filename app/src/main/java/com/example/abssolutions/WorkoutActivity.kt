package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import com.google.firebase.database.*
import android.widget.LinearLayout
import android.widget.TextView
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater

class WorkoutActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.workout_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        userEmail = intent.getStringExtra("USER_EMAIL")

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
                    true
                }
                R.id.navigation_store -> {
                    val intent = Intent(this, StoreActivity::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.navigation_workout

        // Fetch user's body type and then fetch workouts
        fetchUserBodyType()
    }

    private fun fetchUserBodyType() {
        val email = userEmail ?: return
        val userKey = email.replace('.', ',')
        val db = FirebaseDatabase.getInstance().reference
        db.child("users").child(userKey).child("body_type").get()
            .addOnSuccessListener { snapshot ->
                val bodyType = snapshot.getValue(String::class.java)
                if (bodyType != null && bodyType != "Unknown" && bodyType != "Error") {
                    fetchAllWorkoutsFromFirestore(bodyType)
                } else {
                    // Handle case where body type is not available or invalid
                    Log.d("WorkoutActivity", "Body type not found or invalid for user: $email")
                    // Maybe display a default set of exercises or a message
                    fetchAllWorkoutsFromFirestore("DefaultWorkouts") // Example fallback
                }
            }
            .addOnFailureListener {
                Log.e("WorkoutActivity", "Failed to get user body type.", it)
                fetchAllWorkoutsFromFirestore("DefaultWorkouts") // Example fallback on error
            }
    }

    private fun fetchAllWorkoutsFromFirestore(bodyType: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val exerciseListLayout = findViewById<LinearLayout>(R.id.exerciseListLayout)
        exerciseListLayout.removeAllViews()
        Log.d("WorkoutActivity", "Fetching workouts for body type: $bodyType")
        db.collection("exercises") // Query the single "exercises" collection
            .whereEqualTo("type", bodyType) // Filter by body type
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("WorkoutActivity", "No workouts found for body type: $bodyType")
                    // Optionally display a message to the user
                }
                for (document in result) {
                    val name = document.getString("name") ?: continue
                    val sets = document.getString("sets") ?: ""
                    // Inflate the creative card layout
                    val cardView = LayoutInflater.from(this).inflate(R.layout.item_exercise_card, exerciseListLayout, false)
                    val emojiView = cardView.findViewById<TextView>(R.id.textViewEmoji)
                    val nameView = cardView.findViewById<TextView>(R.id.textViewExerciseName)
                    val setsView = cardView.findViewById<TextView>(R.id.textViewExerciseSets)
                    // Optionally, pick emoji based on exercise name
                    val emoji = when {
                        name.contains("push", true) -> "💪"
                        name.contains("press", true) -> "🏋️"
                        name.contains("squat", true) -> "🦵"
                        name.contains("run", true) -> "🏃"
                        name.contains("plank", true) -> "🧘"
                        else -> "🔥"
                    }
                    emojiView.text = emoji
                    nameView.text = name
                    setsView.text = sets
                    exerciseListLayout.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutActivity", "Error getting workouts", exception)
            }
    }
} 