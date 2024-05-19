package ru.dksu.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.dksu.db.entity.StateEntity
import ru.dksu.db.entity.SubscriptionEntity
import ru.dksu.db.entity.UserEntity

@Repository
interface SubscriptionRepository: JpaRepository<SubscriptionEntity, Long>