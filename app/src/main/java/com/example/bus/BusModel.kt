package com.example.bus


data class BusModel(
    val busId: String = "",
    val busName: String = "",
    val from: String = "",
    val to: String = "",
    val date: String = "",
    val departureTime: String = "",
    val price: Int = 0,
    val totalSeats: Int = 30,
    val bookedSeats: Map<String, String> = emptyMap()
)