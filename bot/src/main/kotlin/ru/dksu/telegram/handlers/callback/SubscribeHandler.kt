package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.SubscriptionEntity
import ru.dksu.db.repository.PlaceRepository
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.db.repository.UserRepository
import ru.dksu.telegram.getInlineKeyboard
import kotlin.jvm.optionals.getOrNull


@Component
class SubscribeHandler(
    val subscriptionRepository: SubscriptionRepository,
    val userRepository: UserRepository,
    val placeRepository: PlaceRepository,
): CallbackHandler {
    override val name = HandlerName.SUBSCRIBE

    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        val user = userRepository.findById(callbackQuery.message.chatId).get()
        val subscription = SubscriptionEntity(
            userId = user.id,
            placeFrom = placeRepository.findById(arguments[0]).get(),
            placeTo = placeRepository.findById(arguments[1]).get(),
            date = arguments[2],
            priceLimit = arguments.getOrNull(3)?.toLong()
        )

        user.subscriptions.add(subscription)
        subscriptionRepository.save(subscription)
        userRepository.save(user)

        absSender.execute(
            EditMessageText.builder()
                .chatId(callbackQuery.message.chatId)
                .messageId(callbackQuery.message.messageId)
                .inlineMessageId(callbackQuery.inlineMessageId)
                .text("Вы успешно подписались!")
                .replyMarkup(
                    getInlineKeyboard(listOf(
                        listOf("main" to "В главное меню"),
                    ))
                )
                .build())
    }
}