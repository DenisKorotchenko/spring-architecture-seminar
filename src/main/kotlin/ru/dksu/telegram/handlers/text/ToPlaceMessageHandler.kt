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
class ToPlaceMessageHandler(
    val stateRepository: StateRepository,
    val userRepository: UserRepository,
    val subscriptionRepository: SubscriptionRepository,
    val placeRepository: PlaceRepository,
    val webClient: WebClient,
    val objectMapper: ObjectMapper,
) : TextMessageHandler {
    data class Response1(
        val result: String,
        val RID: String?,
    )

    data class ResponseCar(
        val type: String,
        val freeSeats: Int,
        val tariff: Int,
    )

    data class ResponseTrain(
        val number: String,
        val date0: String,
        val time0: String,
        val date1: String,
        val time1: String,
        val cars: List<ResponseCar>
    )

    override val state = State.TO_PLACE

    @Transactional
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        val placeToEntity = placeRepository.findByName(message.text.uppercase())
        if (placeToEntity == null) {
            absSender.execute(
                SendMessage.builder()
                    .chatId(message.chatId)
                    .text("Станция не найдена, попробуйте ещё раз:")
                    .build()
            )
            return
        }

        val fromPlaceId = state.cache

        state.cache = "$fromPlaceId|${placeToEntity.id}"
        state.stateId = State.DATE.id
        stateRepository.save(state)

        absSender.execute(
            SendMessage.builder()
                .chatId(message.chatId)
                .text("Введите дату отправления (в формате 31.12.2024):")
                .build()
        )
    }
}
