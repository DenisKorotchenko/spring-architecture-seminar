package ru.dksu.dto

data class ResponseTrain(
    val number: String,
    val date0: String,
    val time0: String,
    val date1: String,
    val time1: String,
    val cars: List<ResponseCar>
)

data class ResponseCar(
    val type: String,
    val freeSeats: Int,
    val tariff: Int,
)