package com.example.facultypermission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class LeaveRequestAdapter(
    private val requests: List<LeaveRequest>,
    private val onAction: (Int, String) -> Unit
) : RecyclerView.Adapter<LeaveRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.leaveCard)
        val usernameText: TextView = itemView.findViewById(R.id.usernameText)
        val typeText: TextView = itemView.findViewById(R.id.typeText)
        val timeSpanText: TextView = itemView.findViewById(R.id.timeSpanText)
        val datesText: TextView = itemView.findViewById(R.id.datesText)
        val reasonText: TextView = itemView.findViewById(R.id.reasonText)
        val approveButton: Button = itemView.findViewById(R.id.approveButton)
        val rejectButton: Button = itemView.findViewById(R.id.rejectButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leave_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]

        // Formatting user display
        holder.usernameText.text = request.username ?: "Unknown"

        // Leave type with better format
        holder.typeText.text = "Leave Type: ${request.leaveType ?: "Not Specified"}"

        // Duration format
        holder.timeSpanText.text = "Duration: ${request.timeSpan ?: "N/A"}"

        // Date range format
        holder.datesText.text = "From: ${request.fromDate}  To: ${request.toDate}"

        // Reason formatting
        holder.reasonText.text = "Reason: ${request.reason}"

        // Click actions for Approve/Reject
        holder.approveButton.setOnClickListener { onAction(position, "approve") }
        holder.rejectButton.setOnClickListener { onAction(position, "reject") }
    }

    override fun getItemCount(): Int = requests.size
}
