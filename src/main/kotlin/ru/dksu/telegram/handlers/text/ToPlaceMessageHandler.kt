package ru.dksu.telegram.handlers.text

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.entity.SubscriptionEntity
import ru.dksu.db.repository.StateRepository
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.db.repository.UserRepository
import ru.dksu.telegram.getInlineKeyboard
import ru.dksu.telegram.state.State
import kotlin.jvm.optionals.getOrNull

@Component
class ToPlaceMessageHandler(
    val stateRepository: StateRepository,
    val userRepository: UserRepository,
    val subscriptionRepository: SubscriptionRepository,
) : TextMessageHandler {
    override val state = State.TO_PLACE
    @Transactional
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        val fromPlace = state.cache

        state.stateId = State.START.id
        state.cache = ""
        stateRepository.save(state)

        val user = userRepository.findById(message.chatId).get()
        val subscription = SubscriptionEntity(
            userId = user.id,
            placeFrom = fromPlace,
            placeTo = message.text,
        )
        user.subscriptions.add(subscription)
        subscriptionRepository.save(subscription)
        userRepository.save(user)

        absSender.execute(
            SendMessage(
                message.chatId.toString(),
                "EasyTicketsBot",
            ).apply {
                replyMarkup = getInlineKeyboard(listOf(
                    listOf("search" to "Добавить подписку"),
                ) + (userRepository.findById(message.chatId).getOrNull()?.subscriptions ?: emptySet())
                    .map {
                        listOf("subscription|${it.id}" to "${it.placeFrom} - ${it.placeTo}")
                    }
                )
            })

    }

}