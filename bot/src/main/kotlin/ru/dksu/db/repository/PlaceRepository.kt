package ru.dksu.db.repository

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.dksu.db.entity.PlaceEntity

@Repository
interface PlaceRepository : JpaRepository<PlaceEntity, String> {
    fun findByName(name: String): PlaceEntity?
}