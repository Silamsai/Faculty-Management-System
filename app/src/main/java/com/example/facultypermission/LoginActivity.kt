package com.example.facultypermission

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create LoginRequest object
            val loginRequest = LoginRequest(username, password)

            // Call the login method from ApiService
            RetrofitClient.apiService.login(loginRequest)
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            if (user != null) {
                                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                                // Set the token for future API calls
                                user.token?.let { token ->
                                    RetrofitClient.setToken(token)
                                    Log.d("LoginActivity", "Token set: $token")
                                } ?: run {
                                    Log.e("LoginActivity", "Token is null in login response")
                                }

                                // Compare role case-insensitively and navigate
                                val intent = if (user.role.equals("ADMIN", ignoreCase = true)) {
                                    Intent(this@LoginActivity, AdminHomeActivity::class.java)
                                } else {
                                    Intent(this@LoginActivity, FacultyHomeActivity::class.java)
                                }

                                // Pass the actual name from the database
                                intent.putExtra("name", user.name)
                                intent.putExtra("userId", user.id)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login failed: No user data", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid credentials: ${response.message()}", Toast.LENGTH_SHORT).show()
                            Log.e("LoginActivity", "Login failed: ${response.code()} - ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LoginActivity", "Failed to login", t)
                    }
                })
        }
    }
}

data class LoginRequest(val username: String, val password: String)

data class User(
    val id: Long,
    val username: String,
    val name: String,
    val role: String,
    val token: String?
)