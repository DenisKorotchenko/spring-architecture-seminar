package ru.dksu.telegram.state

enum class State(val id: Long) {
    START(0),
    FROM_PLACE(1),
    TO_PLACE(2),
}