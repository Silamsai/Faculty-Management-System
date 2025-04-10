package com.example.facultypermission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaveHistoryAdapter(private val leaveList: List<LeaveRequest>) :
    RecyclerView.Adapter<LeaveHistoryAdapter.LeaveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leave_history, parent, false)
        return LeaveViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val item = leaveList[position]
        holder.leaveType.text = item.leaveType
        holder.reason.text = item.reason
        holder.status.text = item.status
        holder.fromDate.text = "From: ${item.fromDate}"
        holder.toDate.text = "To: ${item.toDate}"
    }

    override fun getItemCount(): Int = leaveList.size

    class LeaveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leaveType: TextView = view.findViewById(R.id.leaveTypeText)
        val reason: TextView = view.findViewById(R.id.reasonText)
        val status: TextView = view.findViewById(R.id.statusText)
        val fromDate: TextView = view.findViewById(R.id.fromDateText)
        val toDate: TextView = view.findViewById(R.id.toDateText)
    }
}
