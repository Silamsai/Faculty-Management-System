package com.example.facultypermission

data class LeaveRequest(
    val id: Long?,
    val username: String,
    val leaveType: String,
    val timeSpan: String,
    val fromDate: String,
    val toDate: String,
    val reason: String,
    val status: String
)