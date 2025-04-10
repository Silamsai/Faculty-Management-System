package com.example.facultypermission

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FacultyHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.FacultyHomeTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_faculty)

        val name = intent.getStringExtra("name") ?: "Faculty"
        val userId = intent.getLongExtra("userId", 0L)
        val username = intent.getStringExtra("username") ?: "Unknown"
        findViewById<TextView>(R.id.welcomeText).text = "Hello, $name"

        val leaveTypeSpinner = findViewById<Spinner>(R.id.leaveType)
        val timeSpanEditText = findViewById<EditText>(R.id.timeSpan)
        val fromToEditText = findViewById<EditText>(R.id.fromTo)
        val reasonEditText = findViewById<EditText>(R.id.reason)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val previousButton = findViewById<Button>(R.id.previousPermissions)

        // Spinner adapter setup
        ArrayAdapter.createFromResource(
            this,
            R.array.leave_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            leaveTypeSpinner.adapter = adapter
        }

        // Date Picker setup
        fromToEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    fromToEditText.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Time Picker setup
        timeSpanEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val formattedHour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm)
                    timeSpanEditText.setText(formattedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePicker.show()
        }

        // Submit Button Click
        submitButton.setOnClickListener {
            val leaveTypeRaw = leaveTypeSpinner.selectedItem.toString()
            val timeSpan = timeSpanEditText.text.toString()
            val fromTo = fromToEditText.text.toString()
            val reason = reasonEditText.text.toString()

            if (leaveTypeRaw.isEmpty() || timeSpan.isEmpty() || fromTo.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert date to "yyyy-MM-dd"
            val formattedDate = try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.parse(fromTo)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proper leave type enum mapping
            val leaveType = when (leaveTypeRaw) {
                "Casual Leave" -> "CASUAL"
                "Sick Leave" -> "SICK"
                "Emergency" -> "EMERGENCY"
                "Other" -> "OTHER"
                else -> leaveTypeRaw.uppercase()
            }

            val leaveRequestDTO = LeaveRequestDTO(
                userId = userId,
                leaveType = leaveType,
                timeSpan = timeSpan,
                fromDate = formattedDate,
                toDate = formattedDate,
                reason = reason
            )

            Log.d("FacultyHomeActivity", "Submitting DTO: $leaveRequestDTO")

            RetrofitClient.apiService.submitLeaveRequest(leaveRequestDTO).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FacultyHomeActivity, "Leave request submitted!", Toast.LENGTH_SHORT).show()
                        timeSpanEditText.text.clear()
                        fromToEditText.text.clear()
                        reasonEditText.text.clear()
                        leaveTypeSpinner.setSelection(0)
                    } else {
                        Toast.makeText(this@FacultyHomeActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                        Log.e("FacultyHomeActivity", "Backend error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@FacultyHomeActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("FacultyHomeActivity", "Network failure", t)
                }
            })
        }

        previousButton.setOnClickListener {
            val intent = Intent(this, PreviousPermissionsActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }
}
