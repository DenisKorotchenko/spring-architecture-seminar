package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.telegram.getInlineKeyboard

@Component
class BuyHandler: CallbackHandler {
    override val name = HandlerName.MAIN

    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        absSender.execute(
            EditMessageText.builder()
                .chatId(callbackQuery.message.chatId)
                .messageId(callbackQuery.message.messageId)
                .inlineMessageId(callbackQuery.inlineMessageId)
                .text("Ссылка для покупки:\n" +
                        "https://www.rzd.ru/")
                .replyMarkup(
                    getInlineKeyboard(listOf(
                        listOf("main" to "На главную"),
                    ))
                )
                .build())
    }
}