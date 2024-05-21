package ru.dksu.controller

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import jakarta.ws.rs.QueryParam
import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dksu.db.entity.PlaceEntity
import ru.dksu.db.repository.PlaceRepository
import ru.dksu.service.NearestPlaceService
import java.util.*

@RestController
@RequestMapping("/nearestPlace")
class NearestPlaceController(
    val nearestPlaceService: NearestPlaceService,
    val placeRepository: PlaceRepository,
) {
    @GetMapping("/")
    fun get(@QueryParam("str") str: String): List<String> {
        return nearestPlaceService.findNearest(str)
    }

    @GetMapping("/init")
    fun init() {
        placeRepository.run {
            csvReader {
                delimiter = ';'
            }.readAll(ClassPathResource("express.csv").inputStream).drop(1).forEach {
                this.save(
                    PlaceEntity(
                        it.get(2),
                        it.get(4).uppercase(Locale.getDefault())
                    )
                )
            }
        }
    }
}