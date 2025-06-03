package com.example.bus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusAdapter(
    private val busList: List<BusModel>,
    private val onBusClick: (BusModel) -> Unit
) : RecyclerView.Adapter<BusAdapter.BusViewHolder>() {

    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBusInfo: TextView = itemView.findViewById(R.id.tvBusInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = busList[position]
        holder.tvBusInfo.text = "${bus.busName} (${bus.from} → ${bus.to})\nDate: ${bus.date}  Time: ${bus.departureTime}  ৳${bus.price}"

        holder.itemView.setOnClickListener {
            onBusClick(bus)
        }
    }

    override fun getItemCount(): Int = busList.size
}