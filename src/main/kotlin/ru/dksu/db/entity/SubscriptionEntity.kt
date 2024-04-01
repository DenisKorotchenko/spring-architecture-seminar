package ru.dksu.db.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity(name = "subscription")
data class SubscriptionEntity(
    @Id @GeneratedValue
    val id: Long? = null,

    val userId: Long,

    val placeFrom: String,
    val placeTo: String,
)
