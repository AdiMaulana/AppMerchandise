package com.ridexone.appmerchandise

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "app_merchandise.db"
        const val DATABASE_VERSION = 4

        // Tabel Merchandise
        const val TABLE_MERCHANDISE = "m_merchandise"
        const val COLUMN_M_ID = "id"
        const val COLUMN_M_NAME = "name"
        const val COLUMN_M_DESCRIPTION = "description"
        const val COLUMN_M_PRICE = "price"
        const val COLUMN_M_STOCK = "stock"

        // Tabel User
        const val TABLE_USERS = "m_users"
        const val COLUMN_U_ID = "id"
        const val COLUMN_U_FULLNAME = "fullname"
        const val COLUMN_U_PHONE = "phone"
        const val COLUMN_U_EMAIL = "email"
        const val COLUMN_U_USERNAME = "username"
        const val COLUMN_U_PASSWORD = "password"

        // Tabel Transaction
        const val TABLE_TRANSACTION = "t_transaction"
        const val COLUMN_T_ID = "id"
        const val COLUMN_T_MERCHANDISE_ID = "merchandise_id"
        const val COLUMN_T_USER_ID = "user_id"
        const val COLUMN_T_QUANTITY = "quantity"
        const val COLUMN_T_PURCHASE_DATE = "purchase_date"
        const val COLUMN_T_ORDER_NUMBER = "order_number"
        const val COLUMN_T_ORDER_DATE = "order_date"
        const val COLUMN_T_ADDRESS = "address"
        const val COLUMN_T_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("AppDatabaseHelper", "onCreate called")

        val createMerchandiseTable = """
            CREATE TABLE $TABLE_MERCHANDISE (
                $COLUMN_M_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_M_NAME TEXT,
                $COLUMN_M_DESCRIPTION TEXT,
                $COLUMN_M_PRICE REAL,
                $COLUMN_M_STOCK INTEGER
            )
        """.trimIndent()

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_U_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_U_FULLNAME TEXT NOT NULL,
                $COLUMN_U_PHONE TEXT NOT NULL,
                $COLUMN_U_EMAIL TEXT NOT NULL,
                $COLUMN_U_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_U_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        val createTransactionTable = """
            CREATE TABLE $TABLE_TRANSACTION (
                $COLUMN_T_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_T_MERCHANDISE_ID INTEGER,
                $COLUMN_T_USER_ID INTEGER,
                $COLUMN_T_QUANTITY INTEGER,
                $COLUMN_T_PURCHASE_DATE TEXT,
                $COLUMN_T_ORDER_NUMBER TEXT,
                $COLUMN_T_ORDER_DATE TEXT,
                $COLUMN_T_ADDRESS TEXT,
                $COLUMN_T_AMOUNT REAL,
                FOREIGN KEY($COLUMN_T_MERCHANDISE_ID) REFERENCES $TABLE_MERCHANDISE($COLUMN_M_ID),
                FOREIGN KEY($COLUMN_T_USER_ID) REFERENCES $TABLE_USERS($COLUMN_U_ID)
            )
        """.trimIndent()

        db.execSQL(createMerchandiseTable)
        db.execSQL(createUsersTable)
        db.execSQL(createTransactionTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("AppDatabaseHelper", "onUpgrade called from $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MERCHANDISE")
        onCreate(db)
    }

    /* === Merchandise === */
    // Daftar merchandise default yang akan dimasukkan ke database
    private val merchandiseList = listOf(
        Merchandise("DeadSquad - Curse Of The Black Plague", "Kaos resmi DeadSquad dengan desain album Curse Of The Black Plague, bahan cotton combed berkualitas.", 180000.0, 5),
        Merchandise("COLORCODE - Check My Sanity", "Kaos band COLORCODE dengan tema album Check My Sanity, nyaman dipakai sehari-hari.", 155000.0, 2),
        Merchandise("KOIL - Megalo Emperor", "Kaos KOIL dengan desain Megalo Emperor, cocok untuk penggemar musik rock lokal.", 160000.0, 6),
        Merchandise("Revenge The Fate - Sinsera", "Merchandise resmi Revenge The Fate bertema Sinsera, kualitas premium dan limited stock.", 200000.0, 8),
        Merchandise("Eastcape - Obsessed", "Kaos Eastcape dengan desain Obsessed, bahan nyaman dan tahan lama.", 180000.0, 1),
        Merchandise("The Sigit - Another Day", "Merchandise The Sigit bertema Another Day, pilihan tepat untuk koleksi fans sejati.", 160000.0, 2),
        Merchandise("Morfem - Sneakerfuzz", "Kaos Morfem dengan desain Sneakerfuzz, tampil beda dengan gaya unik.", 140000.0, 9),
        Merchandise("Darksovls - Radiusinis", "Merchandise Darksovls bertema Radiusinis, limited edition dan eksklusif.", 160000.0, 3),
        Merchandise("Modern Guns - Everything Falls Apart", "Kaos Modern Guns dengan tema Everything Falls Apart, cocok untuk penggemar musik alternatif.", 160000.0, 2),
        Merchandise("Puupen - Injak Balik!", "Merchandise Puupen dengan desain Injak Balik!, koleksi langka dan bernilai.", 190000.0, 1)
    )

    fun addMerchandise(merchandise: Merchandise): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_M_NAME, merchandise.name)
            put(COLUMN_M_DESCRIPTION, merchandise.description)
            put(COLUMN_M_PRICE, merchandise.price)
            put(COLUMN_M_STOCK, merchandise.stock)
        }
        val result = db.insert(TABLE_MERCHANDISE, null, values)
        db.close()
        return result
    }

    fun getAllMerchandise(): List<Merchandise> {
        val merchandiseList = mutableListOf<Merchandise>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MERCHANDISE", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_M_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_M_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_M_PRICE))
                val stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_M_STOCK))
                merchandiseList.add(Merchandise(name, description, price, stock))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return merchandiseList
    }

    fun updateMerchandiseStock(name: String, newStock: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_M_STOCK, newStock)
        }
        val result = db.update(
            TABLE_MERCHANDISE,
            values,
            "$COLUMN_M_NAME = ?",
            arrayOf(name)
        )
        db.close()
        return result
    }

    // Fungsi untuk memasukkan data merchandiseList ke database jika tabel masih kosong
    fun insertMerchandiseDataIfEmpty() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${AppDatabaseHelper.TABLE_MERCHANDISE}", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()

        if (count == 0) {
            merchandiseList.forEach { merchandise ->
                addMerchandise(merchandise)
            }
        }
    }

    /* === User === */

    fun insertUser(
        fullname: String,
        phone: String,
        email: String,
        username: String,
        password: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_U_FULLNAME, fullname)
            put(COLUMN_U_PHONE, phone)
            put(COLUMN_U_EMAIL, email)
            put(COLUMN_U_USERNAME, username)
            put(COLUMN_U_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM ${AppDatabaseHelper.TABLE_USERS} WHERE $COLUMN_U_USERNAME = ? AND $COLUMN_U_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun verifyUserPassword(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE TRIM($COLUMN_U_USERNAME) = ? AND TRIM($COLUMN_U_PASSWORD) = ?"
        val cursor = db.rawQuery(query, arrayOf(username.trim(), password.trim()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getUserIdByUsername(username: String): Int? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_U_ID),
            "$COLUMN_U_USERNAME = ?",
            arrayOf(username.trim()),
            null, null, null
        )
        val userId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_U_ID))
        } else null
        cursor.close()
        db.close()
        return userId
    }


    /* === Transaction === */

    /**
     * Simpan transaksi pembelian sekaligus update stok merchandise
     */
    fun insertTransactionAndReduceStock(
        merchandiseName: String,
        quantity: Int,
        orderNumber: String,
        orderDate: String,
        address: String,
        userId: Int,
        amount: Double
    ): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            // Dapatkan merchandise_id dan stock dari nama merchandise
            val cursor = db.query(
                TABLE_MERCHANDISE,
                arrayOf(COLUMN_M_ID, COLUMN_M_STOCK),
                "$COLUMN_M_NAME = ?",
                arrayOf(merchandiseName),
                null, null, null
            )
            if (!cursor.moveToFirst()) {
                cursor.close()
                db.endTransaction()
                return false
            }

            val merchandiseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_M_ID))
            val currentStock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_M_STOCK))
            cursor.close()

            if (quantity > currentStock) {
                db.endTransaction()
                return false
            }

            // Insert transaksi dengan user_id dan amount
            val values = ContentValues().apply {
                put(COLUMN_T_MERCHANDISE_ID, merchandiseId)
                put(COLUMN_T_USER_ID, userId)
                put(COLUMN_T_QUANTITY, quantity)
                put(COLUMN_T_PURCHASE_DATE, System.currentTimeMillis().toString())
                put(COLUMN_T_ORDER_NUMBER, orderNumber)
                put(COLUMN_T_ORDER_DATE, orderDate)
                put(COLUMN_T_ADDRESS, address)
                put(COLUMN_T_AMOUNT, amount)
            }
            val insertResult = db.insert(TABLE_TRANSACTION, null, values)
            if (insertResult == -1L) {
                db.endTransaction()
                return false
            }

            // Update stock merchandise
            val newStock = currentStock - quantity
            val updateValues = ContentValues().apply {
                put(COLUMN_M_STOCK, newStock)
            }

            val rowsUpdated = db.update(
                TABLE_MERCHANDISE,
                updateValues,
                "$COLUMN_M_ID = ?",
                arrayOf(merchandiseId.toString())
            )
            if (rowsUpdated < 1) {
                db.endTransaction()
                return false
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            Log.e("AppDatabaseHelper", "Error insertTransactionAndReduceStock", e)
            false
        } finally {
            db.endTransaction()
        }
    }

    /* Optional: fungsi lain sesuai kebutuhan */
}
