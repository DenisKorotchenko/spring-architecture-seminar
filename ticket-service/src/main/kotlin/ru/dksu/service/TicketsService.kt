package ru.dksu.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import ru.dksu.dto.ResponseTrain
import java.lang.Thread.sleep

@Component
class TicketsService(
    val webClient: WebClient,
    val objectMapper: ObjectMapper,
) {
    data class Response1(
        val result: String,
        val RID: String?,
    )

    fun findTickets(fromPlaceId: String, toPlaceId: String, dateString: String): List<ResponseTrain> {
        val response1 = webClient.get().uri { uriBuilder ->
            uriBuilder
                .path("/timetable/public/ru")
                .queryParam("layer_id", "5827")
                .queryParam("dir", "0")
                .queryParam("tfl", "3")
                .queryParam("checkSeats", "1")
                .queryParam("code0", fromPlaceId)
                .queryParam("code1", toPlaceId)
                .queryParam("dt0", dateString)
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

        val trains: List<ResponseTrain> = ((response2!!.body["tp"] as List<Map<String, Any>>).first()["list"] as List<Any>).map {
            objectMapper.readValue<ResponseTrain>(objectMapper.writeValueAsString(it))
        }
        return trains
    }
}