package ru.dksu.telegram

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.dksu.db.repository.StateRepository
import ru.dksu.telegram.handlers.callback.CallbackHandler
import ru.dksu.telegram.handlers.text.TextMessageHandler
import kotlin.jvm.optionals.getOrNull

@Component
class TicketsEasyBot(
    commands: Set<BotCommand>,
    callbackHandlers: Set<CallbackHandler>,
    textMessagesHandlers: Set<TextMessageHandler>,
    @Value("\${telegram.token}")
    token: String,
    @Value("\${telegram.botName")
    val botName: String,
    val stateRepository: StateRepository,
): TelegramLongPollingCommandBot(token) {
    lateinit var callbackHandlersMapping: Map<String, CallbackHandler>
    lateinit var textMessagesHandlersMapping: Map<Long?, TextMessageHandler>

    init {
        registerAll(*commands.toTypedArray())
        callbackHandlersMapping = callbackHandlers.associateBy { it.name.text }
        textMessagesHandlersMapping = textMessagesHandlers.associateBy {
            it.state.id
        }
    }

    override fun getBotUsername(): String = botName

    fun createMessage(chatId: String, text: String) =
        SendMessage(chatId, text)
            .apply { enableMarkdown(true) }
            .apply { disableWebPagePreview() }

    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            val state = stateRepository.findById(update.message.chatId)
            val stateId: Long? = state.getOrNull()?.stateId
            if (!textMessagesHandlersMapping.contains(stateId)) {
                val chatId = update.message.chatId.toString()
                if (update.message.hasText()) {
                    execute(createMessage(chatId, "Вы написали: *${update.message.text}*"))
                } else {
                    execute(createMessage(chatId, "Я понимаю только текст!"))
                }
            } else {
                textMessagesHandlersMapping.getValue(stateId).process(
                    absSender = this,
                    message = update.message,
                    state = state.get(),
                )
            }
        } else if (update.hasCallbackQuery()) {
            execute(AnswerCallbackQuery(update.callbackQuery.id))

            val callbackArguments = update.callbackQuery.data.split("|")
            val callbackName = callbackArguments.first()
            callbackHandlersMapping.getValue(callbackName).processCallbackData(
                absSender = this,
                callbackQuery = update.callbackQuery,
                arguments = callbackArguments.drop(1)
            )
        }
    }
}