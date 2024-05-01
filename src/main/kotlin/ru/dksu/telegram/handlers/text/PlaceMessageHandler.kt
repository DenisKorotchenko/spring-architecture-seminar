package ru.dksu.telegram.handlers.text

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.dksu.service.NearestPlaceService
import ru.dksu.telegram.getInlineKeyboard
import java.util.*

abstract class PlaceMessageHandler(
    open val nearestPlaceService: NearestPlaceService,
): TextMessageHandler {
    fun notFound(absSender: AbsSender, message: Message) {
        val tips = nearestPlaceService.findNearest(message.text.uppercase(Locale.getDefault()))

        val text = if (tips.isEmpty()) "Станция не найдена, попробуйте ещё раз:" else
            "Станция не найдена. Выберите из предложенных вариантов или введите ещё раз"

        absSender.execute(
            SendMessage.builder()
                .chatId(message.chatId)
                .text(text)
                .run {
                    if (tips.isNotEmpty()) {
                        replyMarkup(getInlineKeyboard(
                            tips.map {
                                listOf("place_tip|$it" to it)
                            }
                        ))
                    } else this
                }
                .build()
        )
        return
    }
}