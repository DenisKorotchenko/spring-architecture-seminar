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
class RemoveSubscriptionHandler(
    val subscriptionRepository: SubscriptionRepository,
    val subscriptionsHandler: SubscriptionsHandler,
) : CallbackHandler {
    override val name: HandlerName = HandlerName.REMOVE_SUBSCRIPTION
    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        val subscriptionId = arguments[0]
        val subscription = subscriptionRepository.deleteById(subscriptionId.toLong())
        subscriptionsHandler.processCallbackData(absSender, callbackQuery, arguments)
    }
}