package com.example.mutiara_rpl4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context,


        DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USER = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_CUSTOMER = "customer"
        private const val COLUMN_CUSTOMER_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DOB = "dob"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_BANK_ACCOUNT = "bank_account"

        private const val TABLE_CATEGORY = "category"
        private const val COLUMN_CATEGORY_ID = "id"
        private const val COLUMN_CAT_NAME = "category_name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createUserTable)

        // Sample data
        val sampleInsert = """
            INSERT INTO $TABLE_USER ($COLUMN_USERNAME, $COLUMN_PASSWORD)
            VALUES ('admin', '12345'), ('user', 'password'),('ani','123')
        """
        db.execSQL(sampleInsert)

        val createCustomerTable = """
        CREATE TABLE $TABLE_CUSTOMER (
            $COLUMN_CUSTOMER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_DOB TEXT,
            $COLUMN_PHONE TEXT,
            $COLUMN_EMAIL TEXT,
            $COLUMN_BANK_ACCOUNT TEXT
        )
    """.trimIndent()
        db.execSQL(createCustomerTable)

        val createCategory = """
            CREATE TABLE $TABLE_CATEGORY (
            $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_CAT_NAME TEXT NOT NULL
         )
        """.trimIndent()
        db.execSQL(createCategory)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORY")
        onCreate(db)
    }

    // Fungsi untuk menyimpan (menambahkan) user baru
    fun insertUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }

        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result != -1L // return true jika insert berhasil
    }

    // Fungsi untuk menghapus user berdasarkan username
    fun deleteUser(username: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USER, "$COLUMN_USERNAME = ?", arrayOf(username))
        db.close()
        return result > 0 // return true jika ada baris yang dihapus
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun insertCustomer(name: String, dob: String, phone: String, email: String, bankAccount: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DOB, dob)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_BANK_ACCOUNT, bankAccount)
        }
        val result = db.insert(TABLE_CUSTOMER, null, values)
        db.close()
        return result != -1L
    }
    fun getAllCustomers(): List<Map<String, String>> {
        val db = this.readableDatabase
        val customerList = mutableListOf<Map<String, String>>()

        val cursor = db.rawQuery("SELECT * FROM $TABLE_CUSTOMER", null)
        if (cursor.moveToFirst()) {
            do {
                val customer = mapOf(
                    COLUMN_CUSTOMER_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)).toString(),
                    COLUMN_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    COLUMN_DOB to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB)),
                    COLUMN_PHONE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    COLUMN_BANK_ACCOUNT to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANK_ACCOUNT))
                )
                customerList.add(customer)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return customerList
    }
    fun updateCustomer(id: Int, name: String, dob: String, phone: String, email: String, bankAccount: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DOB, dob)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_BANK_ACCOUNT, bankAccount)
        }

        val result = db.update(TABLE_CUSTOMER, values, "$COLUMN_CUSTOMER_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    fun deleteCustomer(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_CUSTOMER, "$COLUMN_CUSTOMER_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    fun getCustomerById(customerId: Int): Map<String, String> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CUSTOMER,  // Nama tabel customer
            null,  // Ambil semua kolom
            "$COLUMN_ID = ?",  // Kriteria pencarian berdasarkan ID
            arrayOf(customerId.toString()),  // Parameter ID
            null, null, null  // Tidak perlu grouping, sorting, atau limit
        )
        val customer = mutableMapOf<String, String>()

        if (cursor != null && cursor.moveToFirst()) {
            customer["id"] = cursor.getString(cursor.getColumnIndex(COLUMN_ID))
            customer["name"] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            customer["dob"] = cursor.getString(cursor.getColumnIndex(COLUMN_DOB))
            customer["phone"] = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
            customer["email"] = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            customer["bank_account"] = cursor.getString(cursor.getColumnIndex(COLUMN_BANK_ACCOUNT))
        }

        cursor.close()
        db.close()

        return customer
    }

    fun insertCategory(category_name: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CAT_NAME, category_name)
        }

        val result = db.insert(TABLE_CATEGORY, null, values)
        db.close()
        return result != -1L // return true jika insert berhasil
    }

}





