package ru.dksu.db.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "state")
data class StateEntity(
    @Id
    val chatId: Long,

    var stateId: Long,

    var cache: String,
)