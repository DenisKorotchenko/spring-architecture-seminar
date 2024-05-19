package ru.dksu.dto

data class NewTicketsDto(
    val tickets: List<TrainDto>
)

data class TrainDto(
    val number: String,
    val price: Int,
    val departureDate: String,
    val departureTime: String,
    val arrivalDate: String,
    val arrivalTime: String,
)