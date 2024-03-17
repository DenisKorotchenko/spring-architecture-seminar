package ru.dksu.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.dksu.telegram.TicketsEasyBot

@Configuration
class BotConfiguration {
    @Bean
    fun telegramBotsApi(ticketsEasyBot: TicketsEasyBot) : TelegramBotsApi {
        return TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(ticketsEasyBot)
        }
    }
}