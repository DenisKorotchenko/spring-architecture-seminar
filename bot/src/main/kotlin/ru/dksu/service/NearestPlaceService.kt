package ru.dksu.service

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import jakarta.annotation.PostConstruct
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.util.*

@Component
class NearestPlaceService {
    lateinit var placeNames : List<String>

    data class BorNode(val nextNodes: MutableMap<Char, BorNode> = mutableMapOf())
    val rootBorNode = BorNode()

    @PostConstruct
    fun init() {
        placeNames = csvReader {
            delimiter = ';'
        }.readAll(ClassPathResource("express.csv").inputStream).drop(1).map {
            it.get(4).uppercase(Locale.getDefault())
        }

        placeNames.forEach {
            var currentNode = rootBorNode
            (it + "=").forEach { ch ->
                if (currentNode.nextNodes.get(ch) == null) {
                    currentNode.nextNodes.set(ch, BorNode())
                }
                currentNode = currentNode.nextNodes[ch]!!
            }
        }
    }

    fun recursiveSearchInBor(node: BorNode, prefix: String): List<String> {
        val init = if (node.nextNodes.get('=') == null) listOf() else listOf(prefix)
        return node.nextNodes.entries.fold(init) { r, t ->
            r + recursiveSearchInBor(t.value, prefix + t.key)
        }
    }

    fun findInBor(s: String): List<String> {
        var node: BorNode? = rootBorNode
        s.forEach { ch ->
            node = node?.nextNodes?.get(ch)
        }
        val finalNode = node ?: return emptyList()

        return recursiveSearchInBor(finalNode, s)
    }

    fun findNearest(str: String): List<String> {
        return findInBor(str).sortedBy { it.length }.take(3)
    }
}