package ru.dksu.db.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity(name = "subscription")
data class SubscriptionEntity(
    @Id @GeneratedValue
    val id: Long? = null,

    val userId: Long,

    @ManyToOne
    @JoinColumn(name = "placeFromId")
    val placeFrom: PlaceEntity,
    @ManyToOne
    @JoinColumn(name = "placeToId")
    val placeTo: PlaceEntity,
)
