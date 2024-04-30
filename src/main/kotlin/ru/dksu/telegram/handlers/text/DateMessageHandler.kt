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
class DateMessageHandler(
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

    override val state = State.DATE

    @Transactional
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        val date = try {
            val date = LocalDate.parse(message.text, DateTimeFormatter.ofPattern("dd.MM.uuuu"))
            if (ChronoUnit.DAYS.between(LocalDate.now(), date) < 0) {
                absSender.execute(
                    SendMessage.builder()
                        .chatId(message.chatId)
                        .text("Невозможно найти билеты на уже прошедшую дату. Попробуйте ещё раз")
                        .build()
                )
            }
            if (ChronoUnit.DAYS.between(LocalDate.now(), date) > 365) {
                absSender.execute(
                    SendMessage.builder()
                        .chatId(message.chatId)
                        .text("Невозможно найти билеты на более чем 365 дней вперёд. Попробуйте ещё раз")
                        .build()
                )
            }
            date
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
            val response1 = webClient.get().uri { uriBuilder ->
                uriBuilder
                    .path("/timetable/public/ru")
                    .queryParam("layer_id", "5827")
                    .queryParam("dir", "0")
                    .queryParam("tfl", "3")
                    .queryParam("checkSeats", "1")
                    .queryParam("code0", fromPlaceId)
                    .queryParam("code1", toPlaceId)
                    // TODO:
                    .queryParam("dt0", message.text)
                    .queryParam("md", "0")
                    .build()
            }
                .accept(MediaType.ALL)
                .retrieve()
                .onStatus({ e ->
                    !e.is2xxSuccessful
                }, { resp ->
                    resp.bodyToMono(String::class.java).map { RuntimeException(it) }
                })
                .toEntity<Response1>()
                .block()


            val rid = response1?.body?.RID

            if (rid == null) {
                println("NO RID in response")
                throw RuntimeException("No RID in response")
            }

            var response2: ResponseEntity<Map<String, out Any>>? = null
            for (i in 0 until 5) {
                response2 = webClient.post().uri { uriBuilder ->
                    uriBuilder
                        .path("/timetable/public/ru")
                        .queryParam("layer_id", "5827")
                        .build()
                }
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(
                        BodyInserters.fromFormData(
                            "rid", rid
                        )
                    )
                    .retrieve()
                    .toEntity<Map<String, Any>>()
                    .block()!!

                if (response2.body?.get("result") == "OK") {
                    break
                }
                sleep(1000)
            }

            val trains: List<ResponseTrain> =
                ((response2!!.body["tp"] as List<Map<String, Any>>).first()["list"] as List<Any>)
                    .map { objectMapper.readValue<ResponseTrain>(objectMapper.writeValueAsString(it)) }
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
                            minTariff?.let { listOf("subscribeLow|${placeFromEntity.id}|${placeToEntity.id}|$it" to "Подписаться на билеты дешевле $it рублей") } ?: listOf("subscribe|${placeFromEntity.id}|${placeToEntity.id}" to "Подписаться на билеты"),
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
                                listOf("subscribe|${placeFromEntity.id}|${placeToEntity.id}" to "Подписаться на билеты"),
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

fun main() {
    println(ChronoUnit.DAYS.between(
        LocalDate.parse("11.05.2023", DateTimeFormatter.ofPattern("dd.MM.uuuu")),
        LocalDate.parse("11.05.2021", DateTimeFormatter.ofPattern("dd.MM.uuuu"))
    ))
}