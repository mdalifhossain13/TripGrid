package com.example.bus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserBookingAdapter(private val bookings: List<UserBookingModel>) :
    RecyclerView.Adapter<UserBookingAdapter.UserBookingViewHolder>() {

    class UserBookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBookingInfo: TextView = itemView.findViewById(R.id.user_tvBookingInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_booking_item, parent, false)
        return UserBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserBookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvBookingInfo.text = """
            Bus ID: ${booking.busId}
            Seats: ${booking.seats.joinToString(",")}
            Payment: ${booking.paymentMethod}
            Time: ${java.text.SimpleDateFormat("dd MMM, HH:mm").format(java.util.Date(booking.timestamp))}
        """.trimIndent()
    }

    override fun getItemCount(): Int = bookings.size
}
