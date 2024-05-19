package ru.dksu.telegram.handlers.callback

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

interface CallbackHandler {
    val name: HandlerName
    fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>)
}