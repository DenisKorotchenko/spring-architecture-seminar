package ru.dksu.scheduler

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.dksu.controller.SubscriptionsController
import ru.dksu.db.repository.SubscriptionRepository
import ru.dksu.dto.NewTicketsDto
import ru.dksu.dto.TrainDto
import ru.dksu.service.TicketsService

@Component
@EnableScheduling
class SubscriptionsScheduler(
    private val subscriptionRepository: SubscriptionRepository,
    private val ticketsService: TicketsService,
    private val subscriptionsController: SubscriptionsController,
) {
    @Scheduled(fixedDelay = 10000)
    fun checkSubscription() {
        val subscription = subscriptionRepository.findAll().random()
        var trains = ticketsService.findTickets(subscription.placeFrom.id, subscription.placeTo.id, subscription.date)

        if (subscription.priceLimit != null) {
            trains = trains.filter {
                it.cars.map { it.tariff }.min() < subscription.priceLimit
            }
        }

        if (trains.isEmpty()) {
            return
        }

        subscriptionsController.newTickets(subscription.id!!, NewTicketsDto(trains.map {
            TrainDto(
                it.number,
                it.cars.map { it.tariff }.min(),
                it.date0,
                it.time0,
                it.date1,
                it.time1
            )
        }))
    }
}