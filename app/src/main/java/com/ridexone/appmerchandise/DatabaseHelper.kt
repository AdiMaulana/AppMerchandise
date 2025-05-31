package com.ridexone.appmerchandise

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "merchandise_db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MERCHANDISE = "merchandise"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_STOCK = "stock"
    }

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

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MERCHANDISE (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_PRICE REAL,
                $COLUMN_STOCK INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MERCHANDISE")
        onCreate(db)
    }

    // Fungsi untuk menambahkan data Merchandise
    fun addMerchandise(merchandise: Merchandise): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, merchandise.name)
            put(COLUMN_DESCRIPTION, merchandise.description)
            put(COLUMN_PRICE, merchandise.price)
            put(COLUMN_STOCK, merchandise.stock)
        }
        val result = db.insert(TABLE_MERCHANDISE, null, values)
        db.close()
        return result // return row id, -1 jika gagal
    }

    // Fungsi untuk mengambil semua data Merchandise
    fun getAllMerchandise(): List<Merchandise> {
        val merchandiseList = mutableListOf<Merchandise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MERCHANDISE", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK))

                val merchandise = Merchandise(name, description, price, stock)
                merchandiseList.add(merchandise)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return merchandiseList
    }

    fun updateMerchandise(merchandise: Merchandise): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, merchandise.name)
            put(COLUMN_DESCRIPTION, merchandise.description)
            put(COLUMN_PRICE, merchandise.price)
            put(COLUMN_STOCK, merchandise.stock)
        }
        val result = db.update(
            TABLE_MERCHANDISE,
            values,
            "$COLUMN_NAME = ?",
            arrayOf(merchandise.name)
        )
        db.close()
        return result
    }

    fun deleteMerchandiseByName(name: String): Int {
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_MERCHANDISE,
            "$COLUMN_NAME = ?",
            arrayOf(name)
        )
        db.close()
        return result
    }

    fun deleteAllMerchandise() {
        val db = this.writableDatabase
        db.delete(TABLE_MERCHANDISE, null, null)  // Menghapus semua baris tanpa kondisi
        db.close()
    }

    // Fungsi untuk memasukkan data merchandiseList ke database jika tabel masih kosong
    fun insertMerchandiseDataIfEmpty() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_MERCHANDISE", null)
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
}
