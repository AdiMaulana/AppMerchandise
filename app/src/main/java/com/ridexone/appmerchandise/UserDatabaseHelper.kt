package com.ridexone.appmerchandise

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "merchandise_users.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FULLNAME = "fullname"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FULLNAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(
        fullname: String,
        phone: String,
        email: String,
        username: String,
        password: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FULLNAME, fullname)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun verifyUserPassword(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE TRIM($COLUMN_USERNAME) = ? AND TRIM($COLUMN_PASSWORD) = ?"
        val cursor = db.rawQuery(query, arrayOf(username.trim(), password.trim()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }


    // Tambahkan fungsi lain jika perlu, misal cek user login, dll
}
