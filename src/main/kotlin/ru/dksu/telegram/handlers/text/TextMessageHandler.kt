package ru.dksu.telegram.handlers.text

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.telegram.state.State

interface TextMessageHandler {
    val state: State

    fun process(absSender: AbsSender, message: Message, state: StateEntity)
}