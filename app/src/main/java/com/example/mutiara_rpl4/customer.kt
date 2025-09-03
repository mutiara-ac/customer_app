package com.example.mutiara_rpl4

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class customer : AppCompatActivity() {


        private lateinit var btnCreateCustomer: Button
        private lateinit var etSearch: EditText
        private lateinit var customerListLayout: LinearLayout
        private lateinit var dbHelper: DatabaseHelper

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_customer)

            btnCreateCustomer = findViewById(R.id.btnCreateCustomer)
            etSearch = findViewById(R.id.etSearch)
            customerListLayout = findViewById(R.id.customerListLayout)
            dbHelper = DatabaseHelper(this)
            etSearch.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = s.toString()
                    loadCustomers(query)
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })

            btnCreateCustomer.setOnClickListener {
                val intent = Intent(this, Create_customer::class.java)
                startActivity(intent)
                // Tutup LoginActivity agar tidak bisa balik pakai tombol back
                finish()
            }

            loadCustomers()
        }

        private fun loadCustomers(query: String = "") {
            val inflater = LayoutInflater.from(this)
            customerListLayout.removeAllViews()

            val customerList = dbHelper.getAllCustomers()

            val filteredList = if (query.isEmpty()) {
                customerList
            } else {
                customerList.filter {
                    it["name"]?.contains(query, ignoreCase = true) == true
                }
            }

            for (customer in filteredList) {
                val cardView = inflater.inflate(R.layout.activity_item_customer_crad,
                    customerListLayout, false) as CardView

                val nameText = cardView.findViewById<TextView>(R.id.tvCustomerName)
                val dobText = cardView.findViewById<TextView>(R.id.tvCustomerDob)
                val phoneText = cardView.findViewById<TextView>(R.id.tvCustomerPhone)
                val emailText = cardView.findViewById<TextView>(R.id.tvCustomerEmail)
                val accountText = cardView.findViewById<TextView>(R.id.tvCustomerAccount)

                nameText.text = customer["name"]
                dobText.text = "Date of Birth\n${customer["dob"]}"
                phoneText.text = "Phone Number\n${customer["phone"]}"
                emailText.text = "Email\n${customer["email"]}"
                accountText.text = "Bank Account Number\n${customer["bank_account"]}"

                val btnDelete = cardView.findViewById<ImageView>(R.id.btnDelete)

                // Set action untuk tombol delete
                btnDelete.setOnClickListener {
                    // Dialog konfirmasi
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure you want to delete this customer?")
                        .setCancelable(false) // Prevent back press
                        .setPositiveButton("Yes") { dialog, id ->
                            val customerId = customer["id"]?.toInt() ?: return@setPositiveButton
                            val success = dbHelper.deleteCustomer(customerId)

                            if (success) {
                                Toast.makeText(this, "Customer deleted successfully", Toast.LENGTH_SHORT).show()
                                loadCustomers()  // Reload customer list setelah delete
                            } else {
                                Toast.makeText(this, "Failed to delete customer", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("No") { dialog, id ->
                            dialog.dismiss() // Dismiss the dialog if "No" is pressed
                        }

                    // Show the dialog
                    val alert = builder.create()
                    alert.show()
                }
                val editButton = cardView.findViewById<ImageView>(R.id.btnEdit)
                editButton.setOnClickListener {
                    // Mengirimkan ID customer ke halaman UpdateCustomerActivity
                    val intent = Intent(this, update::class.java)
                    intent.putExtra("customerId", customer["id"]?.toInt())  // Kirim ID customer
                    startActivity(intent)
                }

                customerListLayout.addView(cardView)
            }
        }
    }