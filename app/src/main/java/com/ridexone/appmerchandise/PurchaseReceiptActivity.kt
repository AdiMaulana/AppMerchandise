package com.ridexone.appmerchandise

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import com.ridexone.appmerchandise.ui.theme.AppMerchandiseTheme
import com.ridexone.appmerchandise.util.generateQrCode
import java.text.SimpleDateFormat
import java.util.*

class PurchaseReceiptActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val quantity = intent.getIntExtra("quantity", 1)
        val address = intent.getStringExtra("address") ?: ""
        val orderNumber = intent.getStringExtra("orderNumber") ?: generateOrderNumber()
        val orderDate = intent.getStringExtra("orderDate") ?: generateOrderDate()

        setContent {
            AppMerchandiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PurchaseReceiptScreen(
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        address = address,
                        orderNumber = orderNumber,
                        orderDate = orderDate,
                        onFinish = {
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    // Fungsi untuk generate nomor pemesanan unik (contoh sederhana)
    private fun generateOrderNumber(): String {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.getDefault())
    }

    // Fungsi untuk generate tanggal pemesanan sekarang dengan format dd/MM/yyyy HH:mm:ss
    private fun generateOrderDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}

@Composable
fun PurchaseReceiptScreen(
    name: String,
    description: String,
    price: Double,
    quantity: Int,
    address: String,
    orderNumber: String,
    orderDate: String,
    onFinish: () -> Unit
) {
    // Buat data JSON string untuk QR code
    val purchaseDataJson = remember(name, description, price, quantity, address, orderNumber, orderDate) {
        """
        {
            "orderNumber": "$orderNumber",
            "orderDate": "$orderDate",
            "name": "$name",
            "description": "$description",
            "price": $price,
            "quantity": $quantity,
            "address": "$address"
        }
        """.trimIndent()
    }

    // State menyimpan bitmap QR code
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Generate QR code bitmap dengan ukuran lebih besar (500x500)
    LaunchedEffect(purchaseDataJson) {
        qrCodeBitmap = generateQrCode(purchaseDataJson, 500, 500)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Bukti Pembelian", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Nomor Pemesanan: $orderNumber", style = MaterialTheme.typography.titleMedium)
                Text("Tanggal Pemesanan: $orderDate", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nama Barang: $name", style = MaterialTheme.typography.titleMedium)
                Text("Deskripsi: $description", style = MaterialTheme.typography.bodyMedium)
                Text("Harga per unit: Rp ${price.toInt()}", style = MaterialTheme.typography.bodyMedium)
                Text("Jumlah: $quantity", style = MaterialTheme.typography.bodyMedium)
                Text("Total Harga: Rp ${(price * quantity).toInt()}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Alamat Pengiriman:", style = MaterialTheme.typography.titleMedium)
                Text(address, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))

                Text("Scan QR Code :", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                qrCodeBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Scan QR",
                        modifier = Modifier
                            .size(300.dp)  // ukuran tampilan QR Code diperbesar
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Terima kasih telah melakukan pembelian!", style = MaterialTheme.typography.bodyLarge)
            }
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Selesai")
            }
        }
    }
}
