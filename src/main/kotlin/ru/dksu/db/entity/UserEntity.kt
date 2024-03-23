package ru.dksu.db.entity

import jakarta.persistence.*

@Entity(name = "users")
data class UserEntity(
    @Id
    val id: Long,

    val chatId: Long,

    val userName: String,
)