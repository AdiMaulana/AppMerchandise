package com.ridexone.appmerchandise

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.ridexone.appmerchandise.ui.theme.AppMerchandiseTheme

data class Merchandise(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val storeLocation: LatLng = LatLng(-6.200000, 106.816666) // Semua item di Jakarta, misal
)

class DashboardActivity : ComponentActivity() {

    private lateinit var AppDbHelper: AppDatabaseHelper
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDbHelper = AppDatabaseHelper(this)

        // call insert func
        AppDbHelper.insertMerchandiseDataIfEmpty()
        /*
        val updatedMerchandise = Merchandise(
            name = "DeadSquad - Curse Of The Black Plague",
            description = "Desain terbaru, bahan cotton combed premium.",
            price = 185000.0,
            stock = 10
        )
        // call update func
        val rowsUpdated = dbHelper.updateMerchandise(updatedMerchandise)
        if (rowsUpdated > 0) {
            Log.d("DatabaseUpdate", "Update berhasil, jumlah baris yang diupdate: $rowsUpdated")
        } else {
            Log.d("DatabaseUpdate", "Update gagal atau data tidak ditemukan")
        }

        val nameToDelete = "COLORCODE - Check My Sanity"
        // call delete func
        val rowsDeleted = dbHelper.deleteMerchandiseByName(nameToDelete)
        if (rowsDeleted > 0) {
            Log.d("DatabaseDelete", "Data berhasil dihapus, jumlah baris yang dihapus: $rowsDeleted")
        } else {
            Log.d("DatabaseDelete", "Data tidak ditemukan atau gagal dihapus")
        }
        */
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

                        // Ambil data merchandise dari database
                        val merchandiseListDB = AppDbHelper.getAllMerchandise()

                        MerchandiseListScreen(merchandiseListDB, username) { merchandise, quantity ->
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
    var selectedDetailMerchandise by remember { mutableStateOf<Merchandise?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var purchaseQuantity by remember { mutableStateOf("") }
    var showPurchaseDialog by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(merchandiseList) { item ->
            MerchandiseItem(
                item = item,
                onItemClick = {
                    selectedDetailMerchandise = it
                    showDetailDialog = true
                },
                onBuyClick = {
                    selectedDetailMerchandise = it
                    purchaseQuantity = ""
                    showPurchaseDialog = true
                }
            )
        }
    }

    // Detail dialog with maps, no purchase input
    if (showDetailDialog && selectedDetailMerchandise != null) {
        MerchandiseDetailDialog(
            merchandise = selectedDetailMerchandise!!,
            onDismiss = { showDetailDialog = false }
        )
    }

    // Purchase dialog with quantity input (existing)
    if (showPurchaseDialog && selectedDetailMerchandise != null) {
        PurchaseDialog(
            merchandise = selectedDetailMerchandise!!,
            quantity = purchaseQuantity,
            onQuantityChange = { input ->
                if (input.all { it.isDigit() }) {
                    val qty = input.toIntOrNull() ?: 0
                    if (qty <= selectedDetailMerchandise!!.stock) {
                        purchaseQuantity = input
                    }
                } else if (input.isEmpty()) {
                    purchaseQuantity = ""
                }
            },
            onDismiss = { showPurchaseDialog = false },
            onConfirm = {
                val qty = purchaseQuantity.toIntOrNull() ?: 0
                if (qty in 1..selectedDetailMerchandise!!.stock) {
                    onBuyConfirmed(selectedDetailMerchandise!!, qty)
                    showPurchaseDialog = false
                }
            }
        )
    }
}

@Composable
fun MerchandiseItem(
    item: Merchandise,
    onItemClick: (Merchandise) -> Unit,
    onBuyClick: (Merchandise) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val buttonWidth = screenWidth / 2 - 32.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }, // klik seluruh card kecuali tombol
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
                    onClick = {
                        // Cegah event click card agar tidak memicu detail dialog
                        onBuyClick(item)
                    },
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
fun MerchandiseDetailDialog(
    merchandise: Merchandise,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detail Merchandise") },
        text = {
            Column {
                Text(text = merchandise.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = merchandise.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Harga: Rp ${merchandise.price.toInt()}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Stok tersedia: ${merchandise.stock}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Lokasi Store:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                val sampleLocation = LatLng(-6.873666927835847, 107.55257939478201) // Cimahi
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(sampleLocation, 15f)
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = true,
                    )
                ) {
                    Marker(
                        state = MarkerState(position = sampleLocation),
                        title = merchandise.name,
                        snippet = "Jakarta"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
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