package com.example.facultypermission

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @GET("api/leave/admin/pending")
    fun getPendingLeaves(): Call<List<LeaveRequest>>

    // ✅ Cleaned version using @Query for updating status
    @PUT("api/leave/admin/update-status/{id}")
    fun updateLeaveStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): Call<Void>

    @GET("api/leave/faculty/{userId}")
    fun getPreviousPermissions(@Path("userId") userId: Long): Call<List<LeaveRequest>>

    @POST("api/leave/submit")
    fun submitLeaveRequest(
        @Body leaveRequest: LeaveRequestDTO
    ): Call<Void>
}
