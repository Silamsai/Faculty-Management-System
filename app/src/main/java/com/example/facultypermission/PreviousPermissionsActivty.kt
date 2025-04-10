package com.example.facultypermission

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreviousPermissionsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noPermissionsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.FacultyHomeTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_permissions)

        val userId = intent.getLongExtra("userId", 0L)
        val name = intent.getStringExtra("name") ?: "Faculty"
        findViewById<TextView>(R.id.welcomeText).text = "Previous Permissions for $name"

        recyclerView = findViewById(R.id.previousRequests)
        noPermissionsText = findViewById(R.id.noPermissionsText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        fetchPreviousPermissions(userId)
    }

    private fun fetchPreviousPermissions(userId: Long) {
        recyclerView.visibility = View.GONE
        noPermissionsText.visibility = View.GONE

        RetrofitClient.apiService.getPreviousPermissions(userId).enqueue(object : Callback<List<LeaveRequest>> {
            override fun onResponse(call: Call<List<LeaveRequest>>, response: Response<List<LeaveRequest>>) {
                if (response.isSuccessful) {
                    val permissions = response.body() ?: emptyList()
                    Log.d("PreviousPermissions", "Fetched ${permissions.size} permissions: $permissions")
                    if (permissions.isEmpty()) {
                        noPermissionsText.text = "No previous permissions found"
                        noPermissionsText.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noPermissionsText.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.adapter = PreviousRequestAdapter(permissions)
                    }
                } else {
                    Log.e("PreviousPermissions", "Error fetching permissions: ${response.code()} - ${response.message()}")
                    noPermissionsText.text = "Error fetching permissions: ${response.message()}"
                    noPermissionsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<LeaveRequest>>, t: Throwable) {
                Log.e("PreviousPermissions", "Network error: ${t.message}", t)
                noPermissionsText.text = "Network error: ${t.message}"
                noPermissionsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }
}