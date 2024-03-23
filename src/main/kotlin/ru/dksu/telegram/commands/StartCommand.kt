package ru.dksu.telegram.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.UserEntity
import ru.dksu.db.repository.UserRepository

@Component
class StartCommand(
    val userRepository: UserRepository,
): BotCommand(Commands.START.text, "Start command for TicketsEasyBot") {
    override fun execute(sender: AbsSender, user: User, chat: Chat, args: Array<out String>) {
        userRepository.save(UserEntity(
            id = user.id,
            chatId = chat.id,
            userName = user.userName,
        ))
        sender.execute(SendMessage(chat.id.toString(), "Welcome to EasyTickets, ${user.userName}"))
    }
}