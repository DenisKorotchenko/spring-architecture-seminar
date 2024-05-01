package ru.dksu.telegram.handlers.text

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.repository.PlaceRepository
import ru.dksu.db.repository.StateRepository
import ru.dksu.service.NearestPlaceService
import ru.dksu.telegram.state.State

@Component
class ToPlaceMessageHandler(
    override val nearestPlaceService: NearestPlaceService,
    val stateRepository: StateRepository,
    val placeRepository: PlaceRepository,
) : PlaceMessageHandler(nearestPlaceService) {
    override val state = State.TO_PLACE

    @Transactional
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        val placeToEntity = placeRepository.findByName(message.text.uppercase())
        if (placeToEntity == null) {
            notFound(absSender, message)
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
