package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.telegram.getInlineKeyboard

@Component
class MainHandler: CallbackHandler {
    override val name = HandlerName.MAIN

    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        absSender.execute(
            EditMessageText.builder()
                .chatId(callbackQuery.message.chatId)
                .messageId(callbackQuery.message.messageId)
                .inlineMessageId(callbackQuery.inlineMessageId)
                .text("Добро пожаловать в EasyTickets")
                .replyMarkup(
                    getInlineKeyboard(listOf(
                        listOf("search" to "Поиск билетов"),
                        listOf("subscriptions" to "Управление подписками")
                    ))
                )
                .build())
    }
}