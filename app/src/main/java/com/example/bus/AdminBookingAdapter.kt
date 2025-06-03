package com.example.bus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AdminBookingAdapter(private val bookingList: List<BookingModel>) :
    RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBusId: TextView = itemView.findViewById(R.id.tvBusId)
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        val tvPayment: TextView = itemView.findViewById(R.id.tvPayment)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.tvBusId.text = "Bus ID: ${booking.busId}"
        holder.tvUserId.text = "User ID: ${booking.userId}"
        holder.tvPayment.text = "Payment: ${booking.paymentMethod}"
        holder.tvTime.text = "Time: ${formatTimestamp(booking.timestamp)}"
    }

    override fun getItemCount(): Int = bookingList.size

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
