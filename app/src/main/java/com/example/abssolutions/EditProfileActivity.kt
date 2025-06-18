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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

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
    private lateinit var imageViewEditProfile: ImageView
    private lateinit var buttonChangePicture: Button
    private var selectedImageUri: Uri? = null
    private var profileImageBase64: String? = null
    private val PICK_IMAGE_REQUEST = 101

    private var userEmail: String? = null
    private lateinit var database: DatabaseReference

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
        imageViewEditProfile = findViewById(R.id.imageViewEditProfile)
        buttonChangePicture = findViewById(R.id.buttonChangePicture)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

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

        buttonChangePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageViewEditProfile.setImageBitmap(bitmap)
            // Convert to Base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val imageBytes = baos.toByteArray()
            profileImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
    }

    private fun loadUserProfile(email: String) {
        val userKey = email.replace('.', ',')
        database.child("users").child(userKey).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userMap = snapshot.value as? Map<*, *>
                editTextEditName.setText(userMap?.get("name") as? String ?: "")
                editTextEditGender.setText(userMap?.get("gender") as? String ?: "")
                editTextEditWeight.setText((userMap?.get("weight") as? Number)?.toString() ?: "")
                editTextEditHeight.setText((userMap?.get("height") as? Number)?.toString() ?: "")
                editTextEditBMIRate.setText((userMap?.get("bmi_rate") as? Number)?.toString() ?: "")
                editTextEditBirthDate.setText(userMap?.get("birth_date") as? String ?: "")
                editTextEditEmail.setText(userMap?.get("email") as? String ?: email)
                editTextEditAddress.setText(userMap?.get("address") as? String ?: "")
                editTextEditContactNo.setText(userMap?.get("contact_no") as? String ?: "")
                editTextEditMedicalConditions.setText(userMap?.get("medical_conditions") as? String ?: "")
                editTextEditInjuries.setText(userMap?.get("injuries") as? String ?: "")
                editTextEditAllergies.setText(userMap?.get("allergies") as? String ?: "")
                editTextEditCurrentMedications.setText(userMap?.get("current_medications") as? String ?: "")
                editTextEditAdditionalNotes.setText(userMap?.get("additional_notes") as? String ?: "")
                val base64 = userMap?.get("profile_image_base64") as? String
                profileImageBase64 = base64
                if (!base64.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageViewEditProfile.setImageBitmap(bitmap)
                } else {
                    imageViewEditProfile.setImageResource(R.drawable.ic_user_placeholder)
                }
            } else {
                // Set default values if user not found
                editTextEditName.setText("")
                editTextEditGender.setText("")
                editTextEditWeight.setText("0.0")
                editTextEditHeight.setText("0.0")
                editTextEditBMIRate.setText("0.0")
                editTextEditBirthDate.setText("")
                editTextEditEmail.setText(email)
                editTextEditAddress.setText("")
                editTextEditContactNo.setText("")
                editTextEditMedicalConditions.setText("")
                editTextEditInjuries.setText("")
                editTextEditAllergies.setText("")
                editTextEditCurrentMedications.setText("")
                editTextEditAdditionalNotes.setText("")
                imageViewEditProfile.setImageResource(R.drawable.ic_user_placeholder)
            }
        }.addOnFailureListener {
            // Set default values on error
            editTextEditName.setText("")
            editTextEditGender.setText("")
            editTextEditWeight.setText("0.0")
            editTextEditHeight.setText("0.0")
            editTextEditBMIRate.setText("0.0")
            editTextEditBirthDate.setText("")
            editTextEditEmail.setText(email)
            editTextEditAddress.setText("")
            editTextEditContactNo.setText("")
            editTextEditMedicalConditions.setText("")
            editTextEditInjuries.setText("")
            editTextEditAllergies.setText("")
            editTextEditCurrentMedications.setText("")
            editTextEditAdditionalNotes.setText("")
            imageViewEditProfile.setImageResource(R.drawable.ic_user_placeholder)
        }
    }

    private fun saveUserProfile() {
        val name = editTextEditName.text.toString().trim()
        val gender = editTextEditGender.text.toString().trim()
        val weight = editTextEditWeight.text.toString().trim()
        val height = editTextEditHeight.text.toString().trim()
        val bmiRate = editTextEditBMIRate.text.toString().trim()
        val birthDate = editTextEditBirthDate.text.toString().trim()
        val email = editTextEditEmail.text.toString().trim()
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

        // Prepare user profile update map
        val profileMap = mutableMapOf(
            "name" to name,
            "gender" to gender,
            "weight" to (weight.toDoubleOrNull() ?: 0.0),
            "height" to (height.toDoubleOrNull() ?: 0.0),
            "bmi_rate" to (bmiRate.toDoubleOrNull() ?: 0.0),
            "birth_date" to birthDate,
            "address" to address,
            "contact_no" to contactNo,
            "medical_conditions" to medicalConditions,
            "injuries" to injuries,
            "allergies" to allergies,
            "current_medications" to currentMedications,
            "additional_notes" to additionalNotes
        )
        if (!profileImageBase64.isNullOrEmpty()) {
            profileMap["profile_image_base64"] = profileImageBase64!!
        }
        val userKey = email.replace('.', ',')
        database.child("users").child(userKey).updateChildren(profileMap as Map<String, Any>)
            .addOnSuccessListener { _ ->
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(this, "Error updating profile or email not found", Toast.LENGTH_SHORT).show()
            }
    }
} 