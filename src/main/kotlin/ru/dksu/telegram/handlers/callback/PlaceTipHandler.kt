package ru.dksu.telegram.handlers.callback

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.db.repository.StateRepository
import ru.dksu.telegram.handlers.text.FromPlaceMessageHandler
import ru.dksu.telegram.handlers.text.ToPlaceMessageHandler
import ru.dksu.telegram.state.State

@Component
class PlaceTipHandler(
    val fromPlaceMessageHandler: FromPlaceMessageHandler,
    val toPlaceMessageHandler: ToPlaceMessageHandler,
    val stateRepository: StateRepository,
): CallbackHandler {
    override val name: HandlerName = HandlerName.PLACE_TIP

    override fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>) {
        val state = stateRepository.findById(callbackQuery.message.chatId).get()
        if (state.stateId == State.FROM_PLACE.id) {
            fromPlaceMessageHandler.process(absSender, callbackQuery.message.apply { text = arguments[0] }, state)
        } else if (state.stateId == State.TO_PLACE.id) {
            toPlaceMessageHandler.process(absSender, callbackQuery.message.apply { text = arguments[0] }, state)
        }
    }
}