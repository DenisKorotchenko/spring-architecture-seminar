package ru.dksu.controller

import jakarta.ws.rs.QueryParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dksu.dto.ResponseTrain
import ru.dksu.service.TicketsService

@RestController
@RequestMapping("/tickets")
class TicketsController(
    val ticketsService: TicketsService,
) {
    @GetMapping("/")
    fun get(
        @QueryParam("fromPlaceId") fromPlaceId: String,
        @QueryParam("toPlaceId") toPlaceId: String,
        @QueryParam("date") date: String,
    ): List<ResponseTrain> {
        return ticketsService.findTickets(fromPlaceId, toPlaceId, date)
    }
}