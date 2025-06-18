package com.example.abssolutions.data

import android.provider.BaseColumns

object UserContract {
    // Table contents are defined here
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_EMAIL = "email"
        const val COLUMN_NAME_PASSWORD = "password"
        // New column for user type
        const val COLUMN_NAME_USER_TYPE = "user_type" // "Member" or "Patient"
        // New columns for personal and health information
        const val COLUMN_NAME_GENDER = "gender"
        const val COLUMN_NAME_WEIGHT = "weight"
        const val COLUMN_NAME_HEIGHT = "height"
        const val COLUMN_NAME_BMI_RATE = "bmi_rate"
        const val COLUMN_NAME_BIRTH_DATE = "birth_date"
        const val COLUMN_NAME_ADDRESS = "address"
        const val COLUMN_NAME_CONTACT_NO = "contact_no"
        const val COLUMN_NAME_MEDICAL_CONDITIONS = "medical_conditions"
        const val COLUMN_NAME_INJURIES = "injuries"
        const val COLUMN_NAME_ALLERGIES = "allergies"
        const val COLUMN_NAME_CURRENT_MEDICATIONS = "current_medications"
        const val COLUMN_NAME_ADDITIONAL_NOTES = "additional_notes"
    }
} 