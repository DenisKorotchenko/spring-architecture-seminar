package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.repository.StateRepository
import ru.dksu.telegram.getInlineKeyboard
import ru.dksu.telegram.state.State

@Component
class SearchHandler(
    val stateRepository: StateRepository,
): CallbackHandler {
    override val name: HandlerName = HandlerName.SEARCH

    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        val state = stateRepository.findById(callbackQuery.message.chatId).orElseGet {
            StateEntity(
                chatId = callbackQuery.message.chatId,
                stateId = State.FROM_PLACE.id,
                cache = "",
            )
        }.apply {
            stateId = State.FROM_PLACE.id
        }
        stateRepository.save(state)

        absSender.execute(
            EditMessageText.builder()
                .chatId(callbackQuery.message.chatId)
                .messageId(callbackQuery.message.messageId)
                .inlineMessageId(callbackQuery.inlineMessageId)
                .replyMarkup(getInlineKeyboard(emptyList()))
                .text("Введите место отправления:")
                .build()
        )
    }
}