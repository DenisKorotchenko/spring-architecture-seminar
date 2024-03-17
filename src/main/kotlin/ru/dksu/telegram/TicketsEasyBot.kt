package ru.dksu.telegram

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class TicketsEasyBot(
    @Value("\${telegram.token}")
    token: String,
    @Value("\${telegram.botName")
    val botName: String
): TelegramLongPollingCommandBot(token) {
    override fun getBotUsername(): String = botName

    fun createMessage(chatId: String, text: String) =
        SendMessage(chatId, text)
            .apply { enableMarkdown(true) }
            .apply { disableWebPagePreview() }

    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            val chatId = update.message.chatId.toString()
            if (update.message.hasText()) {
                execute(createMessage(chatId, "Вы написали: *${update.message.text}*"))
            } else {
                execute(createMessage(chatId, "Я понимаю только текст!"))
            }
        }
    }
}