package com.example.bus

data class BookingModel(
    val busId: String = "",
    val userId: String = "",
    val paymentMethod: String = "",
    val timestamp: Long = 0L
)