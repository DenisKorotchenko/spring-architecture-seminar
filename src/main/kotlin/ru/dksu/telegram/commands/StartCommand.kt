package ru.dksu.telegram.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.UserEntity
import ru.dksu.db.repository.UserRepository

@Component
class StartCommand(
    val userRepository: UserRepository,
) : BotCommand(Commands.START.text, "Start command for TicketsEasyBot") {
    override fun execute(sender: AbsSender, user: User, chat: Chat, args: Array<out String>) {
        userRepository.save(
            UserEntity(
                id = user.id,
                chatId = chat.id,
                userName = user.userName,
            )
        )
        sender.execute(SendMessage(chat.id.toString(), "Welcome to EasyTickets, ${user.userName}").apply {
            replyMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(
                    listOf(
                        KeyboardRow().apply {
                            add("Поиск билетов")
                        },
                        KeyboardRow().apply {
                            add("Мои подписки")
                        }
                    )
                )
                .oneTimeKeyboard(true)
                .build()
        })
    }
}