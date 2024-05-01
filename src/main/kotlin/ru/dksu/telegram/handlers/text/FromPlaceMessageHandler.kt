package ru.dksu.telegram.handlers.text

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.repository.PlaceRepository
import ru.dksu.db.repository.StateRepository
import ru.dksu.service.NearestPlaceService
import ru.dksu.telegram.state.State

@Component
class FromPlaceMessageHandler(
    override val nearestPlaceService: NearestPlaceService,
    val stateRepository: StateRepository,
    val placeRepository: PlaceRepository,
): PlaceMessageHandler(nearestPlaceService) {
    override val state = State.FROM_PLACE
    override fun process(absSender: AbsSender, message: Message, state: StateEntity) {
        val placeEntity = placeRepository.findByName(message.text.uppercase())
        if (placeEntity == null) {
            notFound(absSender, message)
            return
        }

        state.cache = placeEntity.id
        state.stateId = State.TO_PLACE.id
        stateRepository.save(state)
        absSender.execute(
            SendMessage.builder()
                .chatId(message.chatId)
                .text("Введите место назначения:")
                .build()
        )
    }

}