package ru.dksu.telegram.handlers

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.dto.NewTicketsDto
import ru.dksu.telegram.TicketsEasyBot
import ru.dksu.telegram.createMessageWithInlineButtons

@Component
class ReceiveTicketsHandler(
    private val ticketsEasyBot: TicketsEasyBot,
    private val subscriptionRepository: SubscriptionRepository,
) {
    fun process(subscriptionId: Long, newTicketsDto: NewTicketsDto) {
        val subscription = subscriptionRepository.findById(subscriptionId).get()
        subscriptionRepository.deleteById(subscriptionId)

        val minTariff = newTicketsDto.tickets.map {
            it.price
        }.min()

        (ticketsEasyBot as AbsSender).execute(
            createMessageWithInlineButtons(
                chatId = subscription.userId.toString(),
                text = "Найдены билеты по вашим подпискам!\n" +
                        newTicketsDto.tickets.map {
                            "${it.number}: ${it.departureDate} ${it.departureTime} - ${it.arrivalDate} ${it.arrivalTime}, от ${it.price}"
                        }.joinToString("\n"),
                inlineButtons = listOf(
                    listOf("buy|${subscriptionId}" to "Купить"),
                    listOf("subscribe|${subscription.placeFrom.id}|${subscription.placeTo.id}|${subscription.date}|$minTariff" to "Подписаться на билеты дешевле $minTariff рублей"),
                    listOf("main" to "В главное меню"),
                )
            )
        )
    }
}