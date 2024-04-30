package ru.dksu.telegram.handlers

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.telegram.TicketsEasyBot
import ru.dksu.telegram.createMessageWithInlineButtons

@Component
class ReceiveTicketsHandler(
    private val ticketsEasyBot: TicketsEasyBot,
) {
    fun process(chatId: Long) {
        (ticketsEasyBot as AbsSender).execute(
            createMessageWithInlineButtons(
                chatId = chatId.toString(),
                text = "Найдены билеты по вашим подпискам!",
                inlineButtons = listOf(
                    listOf("search" to "Поиск билетов"),
                    listOf("subscriptions" to "Управление подписками")
                )
            )
        )
    }
}