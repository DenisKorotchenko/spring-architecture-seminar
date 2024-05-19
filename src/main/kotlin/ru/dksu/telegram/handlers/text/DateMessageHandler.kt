package ru.dksu.telegram.handlers.text

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.transaction.Transactional
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import reactor.core.publisher.Mono
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.entity.SubscriptionEntity
import ru.dksu.db.repository.PlaceRepository
import ru.dksu.db.repository.StateRepository
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.db.repository.UserRepository
import ru.dksu.service.TicketsService
import ru.dksu.telegram.getInlineKeyboard
import ru.dksu.telegram.state.State
import java.lang.Thread.sleep
import java.text.DateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Component
class DateMessageHandler(
    val stateRepository: StateRepository,
    val placeRepository: PlaceRepository,
    val webClient: WebClient,
    val objectMapper: ObjectMapper,
    val ticketsService: TicketsService,
) : TextMessageHandler {

    override val state = State.DATE

    @Transactional
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        try {
            val date = LocalDate.parse(message.text, DateTimeFormatter.ofPattern("dd.MM.uuuu"))
            if (ChronoUnit.DAYS.between(LocalDate.now(), date) < 0) {
                absSender.execute(
                    SendMessage.builder()
                        .chatId(message.chatId)
                        .text("Невозможно найти билеты на уже прошедшую дату. Попробуйте ещё раз")
                        .build()
                )
                return
            }
            if (ChronoUnit.DAYS.between(LocalDate.now(), date) > 365) {
                absSender.execute(
                    SendMessage.builder()
                        .chatId(message.chatId)
                        .text("Невозможно найти билеты на более чем 365 дней вперёд. Попробуйте ещё раз")
                        .build()
                )
                return
            }
        } catch (e: RuntimeException) {
            absSender.execute(
                SendMessage.builder()
                    .chatId(message.chatId)
                    .text("Неверный формат. Используйте формат 31.12.2024:")
                    .build()
            )
            return
        }

        val splitCache = state.cache.split('|')
        val fromPlaceId = splitCache[0]
        val toPlaceId = splitCache[1]

        try {
            val trains = ticketsService.findTickets(fromPlaceId, toPlaceId, message.text)
            val tickets = trains.filter {
                it.cars.fold(0) { r, t ->
                    r + t.freeSeats
                } > 0
            }.sortedBy {
                it.cars.minOfOrNull { it.tariff }
            }.take(5)

            val placeFromEntity = placeRepository.findById(fromPlaceId).get()
            val placeToEntity = placeRepository.findById(toPlaceId).get()

            val minTariff = tickets.getOrNull(0)?.cars?.minOfOrNull { it.tariff }

            val text = if (tickets.size > 0) {
                "Найденные билеты по направлению ${placeFromEntity.name} - ${placeToEntity.name}\n" +
                        tickets.map {
                            val fromTariff = it.cars.map { it.tariff }.min()
                            "${it.number}: ${it.date0} ${it.time0} - ${it.date1} ${it.time1}, от ${fromTariff}"
                        }.joinToString("\n")
            } else {
                "Билеты по направление ${placeFromEntity.name} - ${placeToEntity.name} не найдены"
            }

            absSender.execute(
                SendMessage(
                    message.chatId.toString(),
                    text
                ).apply {
                    replyMarkup = getInlineKeyboard(
                        listOfNotNull(
                            minTariff?.let { listOf("subscribe|${placeFromEntity.id}|${placeToEntity.id}|${message.text}|${it + 10}" to "Подписаться на билеты дешевле $it рублей") } ?: listOf("subscribe|${placeFromEntity.id}|${placeToEntity.id}|${message.text}" to "Подписаться на билеты"),
                            listOf("main" to "Главное меню"),
                        )
                    )
                })
        } catch (e: RuntimeException) {
            println("Error: ${e.message}")

            val placeFromEntity = placeRepository.findById(fromPlaceId).get()
            val placeToEntity = placeRepository.findById(toPlaceId).get()

            absSender.execute(
                SendMessage.builder()
                    .chatId(message.chatId)
                    .text("Билеты по направление ${placeFromEntity.name} - ${placeToEntity.name} не найдены")
                    .replyMarkup(
                        getInlineKeyboard(
                            listOf(
                                listOf("subscribe|${placeFromEntity.id}|${placeToEntity.id}|${message.text}" to "Подписаться на билеты"),
                                listOf("main" to "Главное меню"),
                            )
                        )
                    )
                    .build()
            )
        } finally {
            state.stateId = State.START.id
            state.cache = ""
            stateRepository.save(state)
        }
    }
}
