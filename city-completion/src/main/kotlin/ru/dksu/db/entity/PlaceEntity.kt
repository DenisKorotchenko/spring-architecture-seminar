package ru.dksu.db.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "places")
data class PlaceEntity(
    @Id
    val id: String,

    val name: String
)