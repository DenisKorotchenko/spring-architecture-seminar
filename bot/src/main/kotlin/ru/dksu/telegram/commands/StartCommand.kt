package ru.dksu.telegram.commands

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.UserEntity
import ru.dksu.db.repository.UserRepository
import ru.dksu.telegram.createMessageWithInlineButtons

@Component
class StartCommand(
    val userRepository: UserRepository,
) : BotCommand(Commands.START.text, "Start command for TicketsEasyBot") {
    override fun execute(sender: AbsSender, user: User, chat: Chat, args: Array<out String>) {
        logger.info("Start work with user ${user.userName}, id: ${chat.id}")

        userRepository.save(
            UserEntity(
                id = user.id,
                chatId = chat.id,
                userName = user.userName,
                subscriptions = mutableSetOf(),
            )
        )
        sender.execute(
            createMessageWithInlineButtons(
                chatId = chat.id.toString(),
                text = "Welcome to EasyTickets, ${user.userName}!",
                inlineButtons = listOf(
                    listOf("search" to "Поиск билетов"),
                    listOf("subscriptions" to "Управление подписками")
                )
            )
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(StartCommand::class.java)
    }
}