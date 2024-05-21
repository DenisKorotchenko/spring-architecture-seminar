package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.telegram.getInlineKeyboard

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
                    |от ${subscription.placeFrom.name}
                    |до ${subscription.placeTo.name}
                    |на ${subscription.date}
                    |${subscription.priceLimit?.let { "билеты дешевле $it" } ?: "любые билеты"}
                """.trimMargin())
                .replyMarkup(getInlineKeyboard(listOf(
                    listOf("remove_subscription|${subscription.id}" to "Удалить подписку"),
                    listOf("subscriptions" to "Назад"),
                    listOf("main" to "Главное меню"),
                )))
                .build()
            )
    }
}