package ru.dksu.telegram.handlers.callback

enum class HandlerName(val text: String) {
    SUBSCRIPTIONS("subscriptions"),
    SEARCH("search"),
    SUBSCRIPTION("subscription"),
    REMOVE_SUBSCRIPTION("remove_subscription"),
}