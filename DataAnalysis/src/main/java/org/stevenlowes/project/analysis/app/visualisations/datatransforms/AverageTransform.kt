package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.interpolate
import java.util.*

fun List<Map<Long, Double>>.transformAverage(): Map<Long, Double> {
    val newKeys = flatMap { it.keys }.distinct().sorted()
    val seriesValues = map { series -> series.interpolateForKeys(newKeys) }
    return newKeys.map { key ->
        key to seriesValues.mapNotNull { it[key] }.average()
    }.toMap()
}

private fun Map<Long, Double>.interpolateForKeys(keys: List<Long>): Map<Long, Double> {
    val sorted = toList().sortedBy { it.first }
    val indexed = sorted.withIndex().toList()
    var index = 0

    val overIndices = keys.mapNotNull { newKey ->
        val over = indexed.first(index) {
            it.value.first >= newKey
        } ?: return@mapNotNull null

        index = over.index

        if(over.value.first == newKey){
            return@mapNotNull Triple(newKey, over.value, over.value)
        }

        if(over.index == 0){
            return@mapNotNull null
        }

        val under = sorted[index - 1]

        if(under.first >= newKey){
            return@mapNotNull null
        }

        return@mapNotNull Triple(newKey, under, over.value)
    }

    val interpolated = overIndices.mapNotNull { (newKey, under, over) ->
        val interpolated = interpolate(newKey, under, over) ?: return@mapNotNull null
        return@mapNotNull newKey to interpolated
    }.toMap()

    return interpolated
}

private fun <T> List<T>.first(startIndex: Int, predicate: (T) -> Boolean): T? {
    (startIndex until size).forEach {
        val value = get(it)
        if(predicate(value)) return value
    }
    return null
}