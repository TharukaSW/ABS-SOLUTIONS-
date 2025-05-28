package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import com.example.abssolutions.data.UserContract
import com.example.abssolutions.data.UserDbHelper
import android.widget.Button
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    private var userEmail: String? = null

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
                // Add cases for other navigation items
                else -> false
            }
        }

        // Set the selected item in the bottom navigation
        bottomNavigationView.selectedItemId = R.id.navigation_profile
    }

    private fun displayUserProfile(email: String) {
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            UserContract.UserEntry.COLUMN_NAME_NAME,
            UserContract.UserEntry.COLUMN_NAME_USER_TYPE,
            UserContract.UserEntry.COLUMN_NAME_GENDER,
            UserContract.UserEntry.COLUMN_NAME_WEIGHT,
            UserContract.UserEntry.COLUMN_NAME_HEIGHT,
            UserContract.UserEntry.COLUMN_NAME_BMI_RATE,
            UserContract.UserEntry.COLUMN_NAME_BIRTH_DATE,
            UserContract.UserEntry.COLUMN_NAME_ADDRESS,
            UserContract.UserEntry.COLUMN_NAME_CONTACT_NO,
            // Include health info columns if needed to display, though the image didn't show them here
            UserContract.UserEntry.COLUMN_NAME_MEDICAL_CONDITIONS,
            UserContract.UserEntry.COLUMN_NAME_INJURIES,
            UserContract.UserEntry.COLUMN_NAME_ALLERGIES,
            UserContract.UserEntry.COLUMN_NAME_CURRENT_MEDICATIONS,
            UserContract.UserEntry.COLUMN_NAME_ADDITIONAL_NOTES
        )

        val selection = "${UserContract.UserEntry.COLUMN_NAME_EMAIL} = ?"
        val selectionArgs = arrayOf(email)

        val cursor = db.query(
            UserContract.UserEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            if (moveToFirst()) {
                val name = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_NAME))
                val userType = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_USER_TYPE))
                val gender = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_GENDER))
                val weight = getDouble(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_WEIGHT))
                val height = getDouble(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_HEIGHT))
                val bmiRate = getDouble(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_BMI_RATE))
                val address = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_ADDRESS))
                val contactNo = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_CONTACT_NO))
                 // Birth date is stored as TEXT, body type is not explicitly stored

                textViewProfileName.text = name
                textViewUserType.text = userType // Or format based on your design
                textViewHeight.text = "${height} cm"
                textViewWeight.text = "${weight} KG"
                textViewBMIRate.text = bmiRate.toString()
                textViewBodyType.text = "Normal" // Placeholder or fetch if you add a column
                textViewEmail.text = email
                textViewMobileNo.text = contactNo
                textViewAddress.text = address

                // Note: Birth Date, Medical Conditions, Injuries, Allergies, Medications, Notes
                // are fetched but not currently displayed in this layout based on the image.
                // You would need to add TextViews for these if you want to display them.

            } else {
                // User not found in database - should not happen if intent had email
                Toast.makeText(this@UserProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        cursor.close()
        dbHelper.close()
    }
} 