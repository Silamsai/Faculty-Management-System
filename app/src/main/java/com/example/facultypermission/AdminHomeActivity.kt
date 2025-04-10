package com.example.facultypermission

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var welcomeText: TextView
    private lateinit var noRequestsText: TextView
    private lateinit var exportButton: Button

    private var fetchedRequests: List<LeaveRequest> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        welcomeText = findViewById(R.id.welcomeText)
        recyclerView = findViewById(R.id.pendingRequests)
        noRequestsText = findViewById(R.id.noRequestsText)
        exportButton = findViewById(R.id.exportButton)

        val name = intent.getStringExtra("name") ?: "Admin"
        welcomeText.text = "Welcome, $name"

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        exportButton.setOnClickListener {
            if (fetchedRequests.isNotEmpty()) {
                exportToExcel(fetchedRequests)
            } else {
                Toast.makeText(this, "No data to export!", Toast.LENGTH_SHORT).show()
            }
        }

        fetchPendingLeaves()
    }

    private fun fetchPendingLeaves() {
        recyclerView.visibility = View.GONE
        noRequestsText.visibility = View.GONE
        exportButton.visibility = View.GONE
        welcomeText.text = "Loading pending requests..."

        RetrofitClient.apiService.getPendingLeaves().enqueue(object : Callback<List<LeaveRequest>> {
            override fun onResponse(call: Call<List<LeaveRequest>>, response: Response<List<LeaveRequest>>) {
                if (response.isSuccessful) {
                    fetchedRequests = response.body() ?: emptyList()
                    Log.d("AdminHomeActivity", "Fetched ${fetchedRequests.size} pending requests: $fetchedRequests")
                    if (fetchedRequests.isEmpty()) {
                        noRequestsText.text = "No pending leave requests"
                        noRequestsText.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        exportButton.visibility = View.GONE
                    } else {
                        noRequestsText.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        exportButton.visibility = View.VISIBLE
                        recyclerView.adapter = LeaveRequestAdapter(fetchedRequests) { position, action ->
                            when (action) {
                                "approve" -> handleApproval(position, fetchedRequests[position])
                                "reject" -> handleRejection(position, fetchedRequests[position])
                            }
                        }
                    }
                } else {
                    welcomeText.text = "Error fetching requests: ${response.message()}"
                    noRequestsText.text = "Failed to load requests"
                    noRequestsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    exportButton.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<LeaveRequest>>, t: Throwable) {
                Log.e("AdminHomeActivity", "Network error: ${t.message}", t)
                welcomeText.text = "Network error: ${t.message}"
                noRequestsText.text = "Network error occurred"
                noRequestsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                exportButton.visibility = View.GONE
            }
        })
    }

    private fun handleApproval(position: Int, request: LeaveRequest) {
        updateLeaveStatus(request.id ?: return, "APPROVED")
    }

    private fun handleRejection(position: Int, request: LeaveRequest) {
        updateLeaveStatus(request.id ?: return, "DISAPPROVED")
    }

    private fun updateLeaveStatus(leaveId: Long, status: String) {
        RetrofitClient.apiService.updateLeaveStatus(leaveId, status).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminHomeActivity, "Status updated to $status", Toast.LENGTH_SHORT).show()
                    fetchPendingLeaves()
                } else {
                    Log.e("AdminHomeActivity", "Error updating status: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("AdminHomeActivity", "Network error updating status: ${t.message}", t)
            }
        })
    }

    private fun exportToExcel(requests: List<LeaveRequest>) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Leave Requests")

        val header = sheet.createRow(0)
        val headers = listOf("Name", "Type", "From", "To", "Reason", "Status")

        for ((index, title) in headers.withIndex()) {
            header.createCell(index).setCellValue(title)
        }

        for ((i, req) in requests.withIndex()) {
            val row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(req.username)
            row.createCell(1).setCellValue(req.leaveType)
            row.createCell(2).setCellValue(req.timeSpan)
            row.createCell(3).setCellValue(req.fromDate)
            row.createCell(4).setCellValue(req.toDate)
            row.createCell(5).setCellValue(req.reason)
            row.createCell(6).setCellValue(req.status)
        }

        val fileName = "Leave_Requests_${System.currentTimeMillis()}.xlsx"
        val filePath = File(getExternalFilesDir(null), fileName)
        val outputStream = FileOutputStream(filePath)
        workbook.write(outputStream)
        workbook.close()
        outputStream.close()

        Toast.makeText(this, "Exported to: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
    }
}
