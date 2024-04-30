package ru.dksu.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.netty.handler.codec.http.cookie.Cookie
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Bean
    fun webClient(
        objectMapper: ObjectMapper,
    ): WebClient {
        return WebClient.builder()
            .baseUrl("https://pass.rzd.ru")
            .codecs {
                it.customCodecs().registerWithDefaultConfig(
                    Jackson2JsonDecoder(objectMapper, MediaType("text", "javascript"))
                )
            }
            .filter(CookieManager())
            .build()
    }
}
