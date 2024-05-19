package ru.dksu.db.entity

import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType

@Entity(name = "users")
data class UserEntity(
    @Id
    val id: Long,

    val chatId: Long,

    val userName: String,

    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER)
    val subscriptions: MutableSet<SubscriptionEntity>,
)