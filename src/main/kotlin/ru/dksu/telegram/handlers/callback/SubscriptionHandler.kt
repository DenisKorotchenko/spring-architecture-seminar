package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.db.repository.UserRepository
import ru.dksu.telegram.getInlineKeyboard
import kotlin.jvm.optionals.getOrNull

@Component
class SubscriptionHandler(
    val subscriptionRepository: SubscriptionRepository,
) : CallbackHandler {
    override val name: HandlerName = HandlerName.SUBSCRIPTION
    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        val subscriptionId = arguments[0]
        val subscription = subscriptionRepository.findById(subscriptionId.toLong()).get()

        absSender.execute(
            EditMessageText.builder()
                .chatId(callbackQuery.message.chatId)
                .messageId(callbackQuery.message.messageId)
                .inlineMessageId(callbackQuery.inlineMessageId)
                .text("""Подписка:
                    |от ${subscription.placeFrom}
                    |до ${subscription.placeTo}
                """.trimMargin())
                .replyMarkup(getInlineKeyboard(listOf(
                    listOf("remove_subscription|${subscription.id}" to "Удалить подписку"),
                    listOf("subscriptions" to "Назад"),
                )))
                .build()
            )
    }
}