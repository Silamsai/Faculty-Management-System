package com.example.facultypermission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PreviousRequestAdapter(
    val requests: List<LeaveRequest>
) : RecyclerView.Adapter<PreviousRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leaveTypeText: TextView = itemView.findViewById(R.id.leaveTypeText)
        val timeSpanText: TextView = itemView.findViewById(R.id.timeSpanText)
        val datesText: TextView = itemView.findViewById(R.id.datesText)
        val reasonText: TextView = itemView.findViewById(R.id.reasonText)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_previous_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.leaveTypeText.text = "Leave Type: ${request.leaveType}"
        holder.timeSpanText.text = "Time Span: ${request.timeSpan}"
        holder.datesText.text = "Dates: ${request.fromDate} to ${request.toDate}"
        holder.reasonText.text = "Reason: ${request.reason}"

        val status = request.status?.uppercase() ?: "PENDING"

        val icon = when (status) {
            "APPROVED" -> "🟢"
            "DISAPPROVED" -> "❌"
            else -> "⏳"
        }

        holder.statusText.text = "Status: $icon $status"

        val context = holder.itemView.context
        val color = when (status) {
            "APPROVED" -> ContextCompat.getColor(context, R.color.green)
            "DISAPPROVED" -> ContextCompat.getColor(context, R.color.red)
            else -> ContextCompat.getColor(context, R.color.orange)
        }
        holder.statusText.setTextColor(color)
    }

    override fun getItemCount() = requests.size
}
