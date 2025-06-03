package com.example.bus

data class UserBookingModel(
    val userId: String = "",
    val busId: String = "",
    val seats: List<Int> = emptyList(),
    val paymentMethod: String = "",
    val timestamp: Long = 0L
)
