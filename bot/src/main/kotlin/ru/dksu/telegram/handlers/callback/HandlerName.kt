package ru.dksu.telegram.handlers.callback

enum class HandlerName(val text: String) {
    SUBSCRIPTIONS("subscriptions"),
    SEARCH("search"),
    SUBSCRIPTION("subscription"),
    SUBSCRIBE("subscribe"),
    REMOVE_SUBSCRIPTION("remove_subscription"),
    MAIN("main"),
    PLACE_TIP("place_tip")
}