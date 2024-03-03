package ru.dksu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleApplication

fun main(args: Array<String>) {
    val context = runApplication<ExampleApplication>(*args)
    context
}