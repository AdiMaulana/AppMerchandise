package com.ridexone.appmerchandise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ridexone.appmerchandise.ui.theme.AppMerchandiseTheme

class PurchasePasswordActivity : ComponentActivity() {

    private lateinit var AppDbHelper: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDbHelper = AppDatabaseHelper(this)

        val username = intent.getStringExtra("username") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val quantity = intent.getIntExtra("quantity", 1)
        val address = intent.getStringExtra("address") ?: ""

        setContent {
            AppMerchandiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PurchasePasswordScreen(
                        onVerify = { password ->
                            Log.d("DEBUG", "Username: '${username.trim()}', Password input: '${password.trim()}'")
                            val isValid = AppDbHelper.verifyUserPassword(username, password)
                            if (isValid) {
                                // Password benar → simpan transaksi ke database
                                val orderNumber = generateOrderNumber()
                                val orderDate = getCurrentDateString()

                                val userId = AppDbHelper.getUserIdByUsername(username)

                                if (userId == null) {
                                    Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    return@PurchasePasswordScreen // atau tangani error sesuai kebutuhan
                                }

                                val amount = price * quantity

                                val success = AppDbHelper.insertTransactionAndReduceStock(
                                    merchandiseName = name,
                                    quantity = quantity,
                                    orderNumber = orderNumber,
                                    orderDate = orderDate,
                                    address = address,
                                    userId = userId,
                                    amount = amount
                                )

                                if (success) {
                                    // Lanjutkan ke tampilan bukti pembelian
                                    val intent = Intent(this, PurchaseReceiptActivity::class.java).apply {
                                        putExtra("orderNumber", orderNumber)
                                        putExtra("orderDate", orderDate)
                                        putExtra("name", name)
                                        putExtra("description", description)
                                        putExtra("price", price)
                                        putExtra("quantity", quantity)
                                        putExtra("address", address)
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Gagal menyimpan pembelian, stok kurang atau error", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // Fungsi sederhana generate nomor pesanan unik
    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        return "ORD-$timestamp"
    }

    // Fungsi format tanggal saat ini
    private fun getCurrentDateString(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}

@Composable
fun PurchasePasswordScreen(onVerify: (String) -> Unit) {
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Verifikasi Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onVerify(password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = password.isNotBlank()
        ) {
            Text("Konfirmasi")
        }
    }
}
