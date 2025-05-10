package com.ridexone.appmerchandise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ridexone.appmerchandise.ui.theme.AppMerchandiseTheme

class PurchaseConfirmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra("username") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val quantity = intent.getIntExtra("quantity", 1)

        setContent {
            AppMerchandiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PurchaseConfirmScreen(
                        username = username,
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        onNext = { address ->
                            if (address.isBlank()) {
                                Toast.makeText(this, "Alamat pengiriman harus diisi", Toast.LENGTH_SHORT).show()
                            } else {
                                val intent = Intent(this, PurchasePasswordActivity::class.java).apply {
                                    putExtra("username", username)
                                    putExtra("name", name)
                                    putExtra("description", description)
                                    putExtra("price", price)
                                    putExtra("quantity", quantity)
                                    putExtra("address", address)
                                }
                                startActivity(intent)
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseConfirmScreen(
    username: String,
    name: String,
    description: String,
    price: Double,
    quantity: Int,
    onNext: (String) -> Unit
) {
    var address by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Konfirmasi Pembelian", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nama Barang: $name", style = MaterialTheme.typography.titleMedium)
        Text("Deskripsi: $description", style = MaterialTheme.typography.bodyMedium)
        Text("Harga per unit: Rp ${price.toInt()}", style = MaterialTheme.typography.bodyMedium)
        Text("Jumlah: $quantity", style = MaterialTheme.typography.bodyMedium)
        Text("Total Harga: Rp ${(price * quantity).toInt()}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Alamat Pengiriman") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onNext(address) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lanjut")
        }
    }
}
