package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.ContentValues
import com.example.abssolutions.data.UserContract
import com.example.abssolutions.data.UserDbHelper
import android.content.Intent
import android.view.View
import android.widget.LinearLayout

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editTextEditName: EditText
    private lateinit var editTextEditGender: EditText
    private lateinit var editTextEditWeight: EditText
    private lateinit var editTextEditHeight: EditText
    private lateinit var editTextEditBMIRate: EditText
    private lateinit var editTextEditBirthDate: EditText
    private lateinit var editTextEditEmail: EditText
    private lateinit var editTextEditAddress: EditText
    private lateinit var editTextEditContactNo: EditText
    private lateinit var editTextEditMedicalConditions: EditText
    private lateinit var editTextEditInjuries: EditText
    private lateinit var editTextEditAllergies: EditText
    private lateinit var editTextEditCurrentMedications: EditText
    private lateinit var editTextEditAdditionalNotes: EditText
    private lateinit var buttonSaveProfile: Button
    private lateinit var editHealthInfoLayout: LinearLayout

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_profile_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get user email from intent
        userEmail = intent.getStringExtra("USER_EMAIL")

        // Initialize views
        editTextEditName = findViewById(R.id.editTextEditName)
        editTextEditGender = findViewById(R.id.editTextEditGender)
        editTextEditWeight = findViewById(R.id.editTextEditWeight)
        editTextEditHeight = findViewById(R.id.editTextEditHeight)
        editTextEditBMIRate = findViewById(R.id.editTextEditBMIRate)
        editTextEditBirthDate = findViewById(R.id.editTextEditBirthDate)
        editTextEditEmail = findViewById(R.id.editTextEditEmail)
        editTextEditAddress = findViewById(R.id.editTextEditAddress)
        editTextEditContactNo = findViewById(R.id.editTextEditContactNo)
        editTextEditMedicalConditions = findViewById(R.id.editTextEditMedicalConditions)
        editTextEditInjuries = findViewById(R.id.editTextEditInjuries)
        editTextEditAllergies = findViewById(R.id.editTextEditAllergies)
        editTextEditCurrentMedications = findViewById(R.id.editTextEditCurrentMedications)
        editTextEditAdditionalNotes = findViewById(R.id.editTextEditAdditionalNotes)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)
        editHealthInfoLayout = findViewById(R.id.editHealthInfoLayout)

        // Load and display existing user data
        val email = userEmail // Create a local immutable copy
        if (email != null) {
            loadUserProfile(email)
        } else {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            finish() // Close activity if email is missing
        }

        // Set OnClickListener for the Save button
        buttonSaveProfile.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun loadUserProfile(email: String) {
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
                val birthDate = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_BIRTH_DATE))
                val address = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_ADDRESS))
                val contactNo = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_CONTACT_NO))
                val medicalConditions = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_MEDICAL_CONDITIONS))
                val injuries = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_INJURIES))
                val allergies = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_ALLERGIES))
                val currentMedications = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_CURRENT_MEDICATIONS))
                val additionalNotes = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_ADDITIONAL_NOTES))

                // Populate EditText fields
                editTextEditName.setText(name)
                editTextEditGender.setText(gender)
                editTextEditWeight.setText(weight.toString())
                editTextEditHeight.setText(height.toString())
                editTextEditBMIRate.setText(bmiRate.toString())
                editTextEditBirthDate.setText(birthDate)
                editTextEditEmail.setText(email)
                editTextEditAddress.setText(address)
                editTextEditContactNo.setText(contactNo)
                editTextEditMedicalConditions.setText(medicalConditions)
                editTextEditInjuries.setText(injuries)
                editTextEditAllergies.setText(allergies)
                editTextEditCurrentMedications.setText(currentMedications)
                editTextEditAdditionalNotes.setText(additionalNotes)

                // Hide health info if user type is Member
                if (userType == "Member") {
                    editHealthInfoLayout.visibility = View.GONE
                } else {
                    editHealthInfoLayout.visibility = View.VISIBLE
                }

            } else {
                // User not found - should not happen if email is valid
                Toast.makeText(this@EditProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        cursor.close()
        dbHelper.close()
    }

    private fun saveUserProfile() {
        val name = editTextEditName.text.toString().trim()
        val gender = editTextEditGender.text.toString().trim()
        val weight = editTextEditWeight.text.toString().trim()
        val height = editTextEditHeight.text.toString().trim()
        val bmiRate = editTextEditBMIRate.text.toString().trim()
        val birthDate = editTextEditBirthDate.text.toString().trim()
        val email = editTextEditEmail.text.toString().trim() // Get email from the non-editable field
        val address = editTextEditAddress.text.toString().trim()
        val contactNo = editTextEditContactNo.text.toString().trim()
        val medicalConditions = editTextEditMedicalConditions.text.toString().trim()
        val injuries = editTextEditInjuries.text.toString().trim()
        val allergies = editTextEditAllergies.text.toString().trim()
        val currentMedications = editTextEditCurrentMedications.text.toString().trim()
        val additionalNotes = editTextEditAdditionalNotes.text.toString().trim()

        // Basic validation (add more comprehensive validation as needed)
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a writable database instance
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.writableDatabase

        // Create a new map of values for the update
        val values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_NAME_NAME, name)
            put(UserContract.UserEntry.COLUMN_NAME_GENDER, gender)
            put(UserContract.UserEntry.COLUMN_NAME_WEIGHT, if (weight.isNotEmpty()) weight.toDouble() else 0.0)
            put(UserContract.UserEntry.COLUMN_NAME_HEIGHT, if (height.isNotEmpty()) height.toDouble() else 0.0)
            put(UserContract.UserEntry.COLUMN_NAME_BMI_RATE, if (bmiRate.isNotEmpty()) bmiRate.toDouble() else 0.0)
            put(UserContract.UserEntry.COLUMN_NAME_BIRTH_DATE, birthDate)
            put(UserContract.UserEntry.COLUMN_NAME_ADDRESS, address)
            put(UserContract.UserEntry.COLUMN_NAME_CONTACT_NO, contactNo)
            // Update health information fields regardless of user type, as they might have been Patient before
            put(UserContract.UserEntry.COLUMN_NAME_MEDICAL_CONDITIONS, medicalConditions)
            put(UserContract.UserEntry.COLUMN_NAME_INJURIES, injuries)
            put(UserContract.UserEntry.COLUMN_NAME_ALLERGIES, allergies)
            put(UserContract.UserEntry.COLUMN_NAME_CURRENT_MEDICATIONS, currentMedications)
            put(UserContract.UserEntry.COLUMN_NAME_ADDITIONAL_NOTES, additionalNotes)
             // Note: User Type is not changed in this edit form.
        }

        // Which row to update, based on the email
        val selection = "${UserContract.UserEntry.COLUMN_NAME_EMAIL} LIKE ?"
        val selectionArgs = arrayOf(email)

        val count = db.update(
            UserContract.UserEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        if (count > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            // Optionally navigate back to the user profile page or another activity
            finish() // Close EditProfileActivity and return to previous activity (UserProfileActivity)
        } else {
            Toast.makeText(this, "Error updating profile or email not found", Toast.LENGTH_SHORT).show()
        }

        // Close the database helper
        dbHelper.close()
    }
} 