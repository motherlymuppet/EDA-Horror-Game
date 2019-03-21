package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.interpolate
import org.stevenlowes.project.analysis.stdDev
import java.lang.Math.sqrt

fun List<Series>.transformAverage(newName: String, color: Color): Series = Series(
    newName,
    color,
    map { it.data }.run {
        val newKeys = this.flatMap { it.keys }.distinct().sorted()
        val seriesValues = map { data -> data.interpolateForKeys(newKeys) }
        newKeys.map { key ->
            key to seriesValues.mapNotNull { it[key] }.average()
        }.toMap()
    },
    emptyList()
)

fun List<Series>.transformAverageWithError(newName: String, color: Color, errorColor: Color): List<Series> {
    val newKeys = flatMap { it.data.keys }.distinct().sorted()
    val seriesValues = map { series -> series.data.interpolateForKeys(newKeys) }
    val lines = newKeys.map { key ->
        val values = seriesValues.mapNotNull { it[key] }
        val mean = values.average()
        val stdDev = values.stdDev()
        val error = stdDev/sqrt(values.size.toDouble())
        return@map key to Pair(mean, error)
    }

    val mean = lines.map { it.first to it.second.first }
    val errors = lines.map { it.first to it.second.second }

    val meanSeries = Series(
        newName, color, mean.toMap(),
    )

    listOf(
        mean.toMap(),
        mean.zip(errors).map { it.first.first to it.first.second + it. }
    )



    return multipleMaps
}

fun <K,V> List<Map<K, V>>.transformMedian(): Map<K, Double> where K : Comparable<K>, K: Number, V: Comparable<V>, V: Number{
    val newKeys = flatMap { it.keys }.distinct().sorted()
    val seriesValues = map { series -> series.interpolateForKeys(newKeys) }
    return newKeys.map { key ->
        key to seriesValues.mapNotNull { it[key] }.median()
    }.toMap()
}

private fun <T : Comparable<T>> List<T>.median(): T {
    return sorted()[size / 2]
}

private fun <K, V> Map<K, V>.interpolateForKeys(keys: List<K>): Map<K, Double> where K : Comparable<K>, K: Number, V: Number, V:Comparable<V>{
    val sorted = toList().sortedBy { it.first }
    val indexed = sorted.withIndex().toList()
    var index = 0

    val overIndices = keys.mapNotNull { newKey ->
        val over = indexed.first(index) {
            it.value.first >= newKey
        } ?: return@mapNotNull null

        index = over.index

        if (over.value.first == newKey) {
            return@mapNotNull Triple(newKey, over.value, over.value)
        }

        if (over.index == 0) {
            return@mapNotNull null
        }

        val under = sorted[index - 1]

        if (under.first >= newKey) {
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
        if (predicate(value)) return value
    }
    return null
}