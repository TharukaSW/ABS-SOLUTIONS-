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
import android.widget.CheckBox
import android.widget.LinearLayout
import android.view.View
import androidx.core.content.ContextCompat

class PersonalHealthInfoActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextGender: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var editTextBMIRate: EditText
    private lateinit var editTextBirthDate: EditText
    private lateinit var editTextEmailInfo: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextContactNo: EditText
    private lateinit var editTextMedicalConditions: EditText
    private lateinit var editTextInjuries: EditText
    private lateinit var editTextAllergies: EditText
    private lateinit var editTextCurrentMedications: EditText
    private lateinit var editTextAdditionalNotes: EditText
    private lateinit var checkBoxAgreePrivacy: CheckBox
    private lateinit var buttonContinue: Button
    private lateinit var buttonMember: Button
    private lateinit var buttonPatient: Button
    private lateinit var healthInfoLayout: LinearLayout

    private var userEmail: String? = null
    private var currentUserType: String = "Member" // Default user type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_health_info)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personal_health_info_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get email from intent (passed from RegisterActivity)
        userEmail = intent.getStringExtra("USER_EMAIL")

        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextGender = findViewById(R.id.editTextGender)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextBMIRate = findViewById(R.id.editTextBMIRate)
        editTextBirthDate = findViewById(R.id.editTextBirthDate)
        editTextEmailInfo = findViewById(R.id.editTextEmailInfo)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextContactNo = findViewById(R.id.editTextContactNo)
        editTextMedicalConditions = findViewById(R.id.editTextMedicalConditions)
        editTextInjuries = findViewById(R.id.editTextInjuries)
        editTextAllergies = findViewById(R.id.editTextAllergies)
        editTextCurrentMedications = findViewById(R.id.editTextCurrentMedications)
        editTextAdditionalNotes = findViewById(R.id.editTextAdditionalNotes)
        checkBoxAgreePrivacy = findViewById(R.id.checkBoxAgreePrivacy)
        buttonContinue = findViewById(R.id.buttonContinue)
        buttonMember = findViewById(R.id.buttonMember)
        buttonPatient = findViewById(R.id.buttonPatient)
        healthInfoLayout = findViewById(R.id.healthInfoLayout)

        // Pre-fill email if available
        editTextEmailInfo.setText(userEmail)
        editTextEmailInfo.isEnabled = false // Prevent editing email

        // Set initial state of the toggle and health info section
        setToggleState("Member")

        // Set OnClickListener for the toggle buttons
        buttonMember.setOnClickListener { setToggleState("Member") }
        buttonPatient.setOnClickListener { setToggleState("Patient") }

        // Set OnClickListener for the Continue button
        buttonContinue.setOnClickListener {
            savePersonalHealthInfo()
        }
    }

    private fun setToggleState(userType: String) {
        currentUserType = userType
        if (userType == "Member") {
            buttonMember.setBackgroundResource(R.drawable.toggle_left_selected)
            buttonMember.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            buttonPatient.setBackgroundResource(R.drawable.toggle_right_unselected)
            buttonPatient.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            healthInfoLayout.visibility = View.GONE
        } else {
            buttonMember.setBackgroundResource(R.drawable.toggle_left_unselected)
            buttonMember.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            buttonPatient.setBackgroundResource(R.drawable.toggle_right_selected)
            buttonPatient.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            healthInfoLayout.visibility = View.VISIBLE
        }
    }

    private fun savePersonalHealthInfo() {
        val name = editTextName.text.toString().trim()
        val gender = editTextGender.text.toString().trim()
        val weight = editTextWeight.text.toString().trim()
        val height = editTextHeight.text.toString().trim()
        val bmiRate = editTextBMIRate.text.toString().trim()
        val birthDate = editTextBirthDate.text.toString().trim()
        val email = editTextEmailInfo.text.toString().trim()
        val address = editTextAddress.text.toString().trim()
        val contactNo = editTextContactNo.text.toString().trim()
        val medicalConditions = editTextMedicalConditions.text.toString().trim()
        val injuries = editTextInjuries.text.toString().trim()
        val allergies = editTextAllergies.text.toString().trim()
        val currentMedications = editTextCurrentMedications.text.toString().trim()
        val additionalNotes = editTextAdditionalNotes.text.toString().trim()

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || !checkBoxAgreePrivacy.isChecked) {
            Toast.makeText(this, "Please fill required fields and agree to privacy policy", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a writable database instance
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.writableDatabase

        // Create a new map of values for the update
        val values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_NAME_NAME, name)
            put(UserContract.UserEntry.COLUMN_NAME_USER_TYPE, currentUserType) // Save user type
            put(UserContract.UserEntry.COLUMN_NAME_GENDER, gender)
            put(UserContract.UserEntry.COLUMN_NAME_WEIGHT, if (weight.isNotEmpty()) weight.toDouble() else 0.0)
            put(UserContract.UserEntry.COLUMN_NAME_HEIGHT, if (height.isNotEmpty()) height.toDouble() else 0.0)
            put(UserContract.UserEntry.COLUMN_NAME_BMI_RATE, if (bmiRate.isNotEmpty()) bmiRate.toDouble() else 0.0)
            put(UserContract.UserEntry.COLUMN_NAME_BIRTH_DATE, birthDate)
            put(UserContract.UserEntry.COLUMN_NAME_ADDRESS, address)
            put(UserContract.UserEntry.COLUMN_NAME_CONTACT_NO, contactNo)

            // Save health information only if user type is Patient
            if (currentUserType == "Patient") {
                put(UserContract.UserEntry.COLUMN_NAME_MEDICAL_CONDITIONS, medicalConditions)
                put(UserContract.UserEntry.COLUMN_NAME_INJURIES, injuries)
                put(UserContract.UserEntry.COLUMN_NAME_ALLERGIES, allergies)
                put(UserContract.UserEntry.COLUMN_NAME_CURRENT_MEDICATIONS, currentMedications)
                put(UserContract.UserEntry.COLUMN_NAME_ADDITIONAL_NOTES, additionalNotes)
            } else {
                // Optionally clear health info if switching from Patient to Member before saving
                put(UserContract.UserEntry.COLUMN_NAME_MEDICAL_CONDITIONS, "")
                put(UserContract.UserEntry.COLUMN_NAME_INJURIES, "")
                put(UserContract.UserEntry.COLUMN_NAME_ALLERGIES, "")
                put(UserContract.UserEntry.COLUMN_NAME_CURRENT_MEDICATIONS, "")
                put(UserContract.UserEntry.COLUMN_NAME_ADDITIONAL_NOTES, "")
            }
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
            // Redirect to login page
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Clear activity stack
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error updating profile or email not found", Toast.LENGTH_SHORT).show()
        }

        // Close the database helper
        dbHelper.close()
    }
} 