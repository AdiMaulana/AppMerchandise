package com.ridexone.appmerchandise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.ridexone.appmerchandise.ui.theme.AppMerchandiseTheme

class LoginActivity : ComponentActivity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDatabaseHelper(this)

        setContent {
            AppMerchandiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLoginSuccess = { username ->
                            // Simpan username ke SharedPreferences
                            val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            sharedPref.edit().putString("username", username).apply()

                            val intent = Intent(this, DashboardActivity::class.java).apply {
                                putExtra("username", username)
                            }
                            startActivity(intent)
                            finish()
                        },
                        dbHelper = dbHelper
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit, dbHelper: UserDatabaseHelper) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoginEnabled = username.isNotBlank() && password.isNotBlank()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login Member Merchandise",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val isValidUser = dbHelper.checkUser(username, password)
                if (isValidUser) {
                    onLoginSuccess(username.trim())
                } else {
                    Toast.makeText(
                        context,
                        "Username atau password salah",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = isLoginEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Belum punya akun? Daftar di sini",
            color = Color(0xFF1976D2),
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }
                .padding(8.dp)
        )
    }
}
