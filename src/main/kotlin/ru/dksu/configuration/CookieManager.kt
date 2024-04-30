package ru.dksu.configuration

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseCookie
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class CookieManager : ExchangeFilterFunction {
    private val cookies: MutableMap<String, ResponseCookie> = HashMap()

    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> =
        next.exchange(withClientCookies(request)).doOnSuccess { response: ClientResponse ->
            if (request.method() == HttpMethod.GET) {
                response.cookies().values.forEach { cookies ->
                    cookies.forEach {
                        if (it.maxAge.isZero) {
                            this.cookies.remove(it.name)
                        } else {
                            this.cookies[it.name] = it
                        }
                    }
                }
            }
        }

    private fun withClientCookies(request: ClientRequest): ClientRequest =
        ClientRequest.from(request).cookies { it.addAll(clientCookies()) }.build()


    private fun clientCookies(): MultiValueMap<String, String> {
        val result: MultiValueMap<String, String> = LinkedMultiValueMap(cookies.size)
        cookies.values.forEach {
            result.add(
                it.name,
                it.value
            )
        }
        return result
    }
}