package com.example.facultypermission

data class LeaveRequestDTO(
    val userId: Long,
    val leaveType: String,
    val timeSpan: String,
    val fromDate: String, // format: yyyy-MM-dd
    val toDate: String,
    val reason: String
)
