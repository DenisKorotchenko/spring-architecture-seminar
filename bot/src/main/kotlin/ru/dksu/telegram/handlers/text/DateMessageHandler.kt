package ru.dksu.telegram.handlers.text

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.repository.PlaceRepository
import ru.dksu.db.repository.StateRepository
import ru.dksu.dto.ResponseTrain
import ru.dksu.telegram.getInlineKeyboard
import ru.dksu.telegram.state.State
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Component
class DateMessageHandler(
    val stateRepository: StateRepository,
    val placeRepository: PlaceRepository,
    val webClient: WebClient,
) : TextMessageHandler {
    override val state = State.DATE

    private fun findTickets(fromId: String, toId: String, date: String): List<ResponseTrain> {
        return webClient.get()
            .uri("http://localhost:8085/example/tickets/?fromPlaceId=$fromId&toPlaceId=$toId&date=$date")
            .retrieve()
            .toEntity<List<ResponseTrain>>()
            .block()?.body ?: emptyList()
    }

    private fun checkDate(absSender: AbsSender, message: Message): Boolean {
        try {
            val date = LocalDate.parse(message.text, DateTimeFormatter.ofPattern("dd.MM.uuuu"))
            if (ChronoUnit.DAYS.between(LocalDate.now(), date) < 0) {
                absSender.execute(
                    SendMessage.builder()
                        .chatId(message.chatId)
                        .text("Невозможно найти билеты на уже прошедшую дату. Попробуйте ещё раз")
                        .build()
                )
                return false
            }
            if (ChronoUnit.DAYS.between(LocalDate.now(), date) > 365) {
                absSender.execute(
                    SendMessage.builder()
                        .chatId(message.chatId)
                        .text("Невозможно найти билеты на более чем 365 дней вперёд. Попробуйте ещё раз")
                        .build()
                )
                return false
            }
            return true
        } catch (e: RuntimeException) {
            absSender.execute(
                SendMessage.builder()
                    .chatId(message.chatId)
                    .text("Неверный формат. Используйте формат 31.12.2024:")
                    .build()
            )
            return false
        }
    }

    @Transactional
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        if (!checkDate(absSender, message))
            return

        val splitCache = state.cache.split('|')
        val fromPlaceId = splitCache[0]
        val toPlaceId = splitCache[1]

        try {
            val trains = findTickets(fromPlaceId, toPlaceId, message.text)
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
            logger.error(e.message, e)

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

    companion object {
        private val logger = LoggerFactory.getLogger(DateMessageHandler::class.java)
    }
}
