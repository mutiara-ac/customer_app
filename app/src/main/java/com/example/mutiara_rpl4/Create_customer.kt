package com.example.mutiara_rpl4
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mutiara_rpl4.R


class Create_customer : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDob: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAccount: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_customer)

        etName = findViewById(R.id.Name)
        etDob = findViewById(R.id.Dob)
        etPhone = findViewById(R.id.Phone)
        etEmail = findViewById(R.id.Email)
        etAccount = findViewById(R.id.Account)
        btnSave = findViewById(R.id.btn)

        val dbHelper = DatabaseHelper(this)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val dob = etDob.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val account = etAccount.text.toString()

            if (name.isBlank() || dob.isBlank() || phone.isBlank() || email.isBlank() || account.isBlank()) {
                Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertCustomer(name, dob, phone, email, account)
            if (success) {
                Toast.makeText(this, "Customer created successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, customer::class.java)
                startActivity(intent)
                finish() // Kembali ke halaman sebelumnya
            } else {
                Toast.makeText(this, "Failed to create customer", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
