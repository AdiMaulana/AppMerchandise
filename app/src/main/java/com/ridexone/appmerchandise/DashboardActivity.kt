package com.ridexone.appmerchandise

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ridexone.appmerchandise.ui.theme.AppMerchandiseTheme

data class Merchandise(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int
)

class DashboardActivity : ComponentActivity() {

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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "User") ?: "User"

        setContent {
            AppMerchandiseTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "Selamat datang kembali, $username",
                                    style = MaterialTheme.typography.bodyMedium, // Lebih kecil
                                    color = MaterialTheme.colorScheme.primary,   // Warna sama dengan tombol
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            actions = {
                                IconButton(onClick = {
                                    val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                    sharedPref.edit().remove("username").apply()
                                    // Logout: kembali ke LoginActivity dan hapus stack
                                    val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = "Logout"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "List Merchandise Stock Terbaru",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        MerchandiseListScreen(merchandiseList, username) { merchandise, quantity ->
                            val intent = Intent(this@DashboardActivity, PurchaseConfirmActivity::class.java).apply {
                                putExtra("username", username)
                                putExtra("name", merchandise.name)
                                putExtra("description", merchandise.description)
                                putExtra("price", merchandise.price)
                                putExtra("quantity", quantity)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MerchandiseListScreen(
    merchandiseList: List<Merchandise>,
    username: String,
    onBuyConfirmed: (Merchandise, Int) -> Unit
) {
    var selectedMerchandise by remember { mutableStateOf<Merchandise?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var purchaseQuantity by remember { mutableStateOf("") }

    androidx.compose.foundation.lazy.LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(merchandiseList) { item ->
            MerchandiseItem(item) {
                selectedMerchandise = it
                purchaseQuantity = ""
                showDialog = true
            }
        }
    }

    if (showDialog && selectedMerchandise != null) {
        PurchaseDialog(
            merchandise = selectedMerchandise!!,
            quantity = purchaseQuantity,
            onQuantityChange = { input ->
                if (input.all { it.isDigit() }) {
                    val qty = input.toIntOrNull() ?: 0
                    if (qty <= selectedMerchandise!!.stock) {
                        purchaseQuantity = input
                    }
                } else if (input.isEmpty()) {
                    purchaseQuantity = ""
                }
            },
            onDismiss = { showDialog = false },
            onConfirm = {
                val qty = purchaseQuantity.toIntOrNull() ?: 0
                if (qty in 1..selectedMerchandise!!.stock) {
                    onBuyConfirmed(selectedMerchandise!!, qty)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun MerchandiseItem(item: Merchandise, onBuyClick: (Merchandise) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val buttonWidth = screenWidth / 2 - 32.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Harga: Rp ${item.price.toInt()}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Stok tersedia: ${item.stock}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onBuyClick(item) },
                    enabled = item.stock > 0,
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text("Beli")
                }
            }
        }
    }
}

@Composable
fun PurchaseDialog(
    merchandise: Merchandise,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detail Pembelian") },
        text = {
            Column {
                Text(text = merchandise.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = merchandise.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Harga: Rp ${merchandise.price.toInt()}")
                Text(text = "Stok tersedia: ${merchandise.stock}")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Jumlah yang ingin dibeli") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = quantity.toIntOrNull()?.let { it in 1..merchandise.stock } == true
            ) {
                Text("Konfirmasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
