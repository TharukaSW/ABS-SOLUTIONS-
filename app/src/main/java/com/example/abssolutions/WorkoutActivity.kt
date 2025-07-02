package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import android.widget.LinearLayout
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class WorkoutActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

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

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
    }

    private fun fetchUserBodyType() {
        val email = userEmail ?: return
        Log.d("WorkoutActivity", "Fetching member for email: $email")
        val dbRef = FirebaseDatabase.getInstance().getReference("members")
        dbRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("WorkoutActivity", "Members snapshot exists: ${snapshot.exists()} count: ${snapshot.childrenCount}")
                    if (snapshot.exists()) {
                        val member = snapshot.children.first()
                        val memberData = member.value as? Map<*, *>
                        val bodyType = memberData?.get("bodyType") as? String ?: "Unknown"
                        Log.d("WorkoutActivity", "Fetched bodyType: $bodyType for email: $email")
                        fetchAllWorkoutsFromRealtimeDb(bodyType)
                    } else {
                        Log.d("WorkoutActivity", "No member found for email: $email")
                        fetchAllWorkoutsFromRealtimeDb("Unknown")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("WorkoutActivity", "Error fetching member: ${error.message}")
                    fetchAllWorkoutsFromRealtimeDb("Unknown")
                }
            })
    }

    private fun fetchAllWorkoutsFromRealtimeDb(bodyType: String) {
        val db = FirebaseDatabase.getInstance().getReference("exercises")
        val exerciseListLayout = findViewById<LinearLayout>(R.id.exerciseListLayout)
        exerciseListLayout.removeAllViews()
        Log.d("WorkoutActivity", "Fetching workouts for body type: $bodyType from Realtime DB")
        db.orderByChild("type").equalTo(bodyType)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("WorkoutActivity", "Exercises snapshot exists: ${snapshot.exists()} count: ${snapshot.childrenCount}")
                    if (!snapshot.exists()) {
                        Log.d("WorkoutActivity", "No workouts found for body type: $bodyType")
                        // Optionally display a message to the user
                        return
                    }
                    var exerciseCount = 0
                    for (exerciseSnap in snapshot.children) {
                        val exercise = exerciseSnap.value as? Map<*, *> ?: continue
                        val name = exercise["name"] as? String ?: continue
                        val sets = exercise["sets"] as? String ?: ""
                        // Inflate the creative card layout
                        val cardView = LayoutInflater.from(this@WorkoutActivity).inflate(R.layout.item_exercise_card, exerciseListLayout, false)
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
                        exerciseCount++
                    }
                    Log.d("WorkoutActivity", "Displayed $exerciseCount exercises for body type: $bodyType")
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("WorkoutActivity", "Error getting workouts from Realtime DB", error.toException())
                }
            })
    }
} 