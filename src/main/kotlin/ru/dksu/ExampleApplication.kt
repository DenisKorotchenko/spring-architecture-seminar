package ru.dksu

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.ClassPathResource
import ru.dksu.db.entity.PlaceEntity
import ru.dksu.db.repository.PlaceRepository
import java.util.*

@SpringBootApplication
class ExampleApplication

fun main(args: Array<String>) {
    val context = runApplication<ExampleApplication>(*args)
    context.getBean<PlaceRepository>().run {
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