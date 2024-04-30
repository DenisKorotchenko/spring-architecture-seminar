package ru.dksu.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.dksu.telegram.handlers.ReceiveTicketsHandler

@RestController("/subscriptions")
class SubscriptionsController(
    private val receiveTicketsHandler: ReceiveTicketsHandler,
) {

    @PostMapping("/{subscriptionId}/tickets")
    fun newTickets(
        @PathVariable subscriptionId: Long
    ) {
        receiveTicketsHandler.process(subscriptionId)
    }
}