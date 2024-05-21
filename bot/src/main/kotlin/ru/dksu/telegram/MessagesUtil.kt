package ru.dksu.telegram

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

fun createMessageWithInlineButtons(chatId: String, text: String, inlineButtons: List<List<Pair<String, String>>>): SendMessage {
    return SendMessage(chatId, text).apply {
        replyMarkup = getInlineKeyboard(inlineButtons)
    }
}

fun getInlineKeyboard(inlineButtons: List<List<Pair<String, String>>>): InlineKeyboardMarkup {
    return InlineKeyboardMarkup().apply {
        keyboard = inlineButtons.map { rowButtons ->
            rowButtons.map { (data, buttonText) ->
                InlineKeyboardButton.builder()
                    .text(buttonText)
                    .callbackData(data)
                    .build()
            }
        }
    }
}