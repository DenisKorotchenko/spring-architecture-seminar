package ru.dksu.controller

import jakarta.ws.rs.QueryParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dksu.service.NearestPlaceService

@RestController
@RequestMapping("/nearestPlace")
class NearestPlaceController(
    val nearestPlaceService: NearestPlaceService
) {
    @GetMapping("/")
    fun get(@QueryParam("str") str: String): List<String> {
        return nearestPlaceService.findNearest(str)
    }
}