package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.repository.UserRepository
import ru.dksu.telegram.getInlineKeyboard
import kotlin.jvm.optionals.getOrNull

@Component
class SubscriptionsHandler(
    val userRepository: UserRepository,
) : CallbackHandler {
    override val name: HandlerName = HandlerName.SUBSCRIPTIONS

    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        absSender.execute(
            EditMessageText.builder()
                .chatId(callbackQuery.message.chatId)
                .messageId(callbackQuery.message.messageId)
                .inlineMessageId(callbackQuery.inlineMessageId)
                .text("Управление подписками")
                .replyMarkup(
                    getInlineKeyboard(listOf(
                        listOf("search" to "Добавить подписку"),
                    ) + (userRepository.findById(callbackQuery.message.chatId).getOrNull()?.subscriptions ?: emptySet())
                        .map {
                            var text = "${it.placeFrom.name} - ${it.placeTo.name}, ${it.date}"
                            if (it.priceLimit != null) {
                                text += ", дешевле ${it.priceLimit}"
                            }
                            listOf("subscription|${it.id}" to text)
                        })
                )
                .build()
        )
    }
}