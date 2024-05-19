package ru.dksu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CityCompletionApplication

fun main(args: Array<String>) {
    runApplication<CityCompletionApplication>(*args)
}