package com.example.abssolutions.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

import com.example.abssolutions.data.UserContract.UserEntry

private val SQL_CREATE_ENTRIES = """
    CREATE TABLE ${UserEntry.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${UserEntry.COLUMN_NAME_NAME} TEXT,
        ${UserEntry.COLUMN_NAME_EMAIL} TEXT UNIQUE,
        ${UserEntry.COLUMN_NAME_PASSWORD} TEXT,
        ${UserEntry.COLUMN_NAME_USER_TYPE} TEXT,
        ${UserEntry.COLUMN_NAME_GENDER} TEXT,
        ${UserEntry.COLUMN_NAME_WEIGHT} REAL,
        ${UserEntry.COLUMN_NAME_HEIGHT} REAL,
        ${UserEntry.COLUMN_NAME_BMI_RATE} REAL,
        ${UserEntry.COLUMN_NAME_BIRTH_DATE} TEXT,
        ${UserEntry.COLUMN_NAME_ADDRESS} TEXT,
        ${UserEntry.COLUMN_NAME_CONTACT_NO} TEXT,
        ${UserEntry.COLUMN_NAME_MEDICAL_CONDITIONS} TEXT,
        ${UserEntry.COLUMN_NAME_INJURIES} TEXT,
        ${UserEntry.COLUMN_NAME_ALLERGIES} TEXT,
        ${UserEntry.COLUMN_NAME_CURRENT_MEDICATIONS} TEXT,
        ${UserEntry.COLUMN_NAME_ADDITIONAL_NOTES} TEXT
    )
"""

private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}"

class UserDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over.
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "UserReader.db"
    }
} 