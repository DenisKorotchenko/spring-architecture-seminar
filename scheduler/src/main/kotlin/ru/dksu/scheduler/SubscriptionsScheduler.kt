package ru.dksu.scheduler

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.dto.NewTicketsDto
import ru.dksu.dto.TrainDto
import ru.dksu.service.TicketsService

@Component
@EnableScheduling
class SubscriptionsScheduler(
    private val subscriptionRepository: SubscriptionRepository,
    private val ticketsService: TicketsService,
    @Qualifier("internal") private val internalWebClient: WebClient
) {
    @Scheduled(fixedDelay = 10000)
    fun checkSubscription() {
        val subscription = subscriptionRepository.findAll().randomOrNull() ?: return

        var trains = ticketsService.findTickets(subscription.placeFrom.id, subscription.placeTo.id, subscription.date)

        if (subscription.priceLimit != null) {
            trains = trains.filter {
                it.cars.map { it.tariff }.min() < subscription.priceLimit
            }
        }

        if (trains.isEmpty()) {
            return
        }

        internalWebClient.post()
            .uri("http://localhost:8083/example/${subscription.id}/tickets")
            .bodyValue(NewTicketsDto(trains.map {
                TrainDto(
                    it.number,
                    it.cars.map { it.tariff }.min(),
                    it.date0,
                    it.time0,
                    it.date1,
                    it.time1
                )
            }))
            .retrieve().toBodilessEntity().block()
    }
}