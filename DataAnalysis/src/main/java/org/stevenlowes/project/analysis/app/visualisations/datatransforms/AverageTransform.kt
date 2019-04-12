package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.*
import java.lang.Math.sqrt

fun List<Series>.transformAverage(newName: String? = null, color: Color? = null): Series = Series(
    newName ?: first().name + " (Avg)",
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

fun List<Series>.transformAverageWithError(
    newName: String? = null,
    color: Color? = null,
    errorColor: Color? = null
): List<Series> {
    val newKeys = flatMap { it.data.keys }.distinct().sorted()
    val seriesValues = map { series -> series.data.interpolateForKeys(newKeys) }
    val lines = newKeys.map { key ->
        val values = seriesValues.mapNotNull { it[key] }
        val mean = values.average()
        val stdDev = values.stdDev()
        val error = stdDev / sqrt(values.size.toDouble())
        return@map key to Pair(mean, error)
    }

    val mean = lines.map { it.first to it.second.first }.toMap()
    val errors = lines.map { it.first to it.second.second }.toMap()

    val baseName = newName ?: first().name

    val meanSeries = Series(baseName, color, mean, emptyList())
    val lowError = Series(
        "$baseName (Low Error)",
        errorColor,
        mean.mapValues { (key, value) ->
            value - errors.getValue(key)
        },
        emptyList()
    )
    val highError = Series(
        "$baseName (High Error)",
        errorColor,
        mean.mapValues { (key, value) ->
            value + errors.getValue(key)
        },
        emptyList()
    )

    return listOf(lowError, meanSeries, highError)
}

fun List<Series>.transformMedian(newName: String? = null, color: Color? = null): Series {
    val newKeys = flatMap { it.data.keys }.distinct().sorted()
    val seriesValues = map { series -> series.data.interpolateForKeys(newKeys) }
    val newData = newKeys.map { key ->
        key to seriesValues.mapNotNull { it[key] }.median()
    }.toMap()

    return Series(
        newName ?: first().name,
        color,
        newData,
        emptyList()
    )
}

private fun <K, V> Map<K, V>.interpolateForKeys(keys: List<K>): Map<K, Double> where K : Comparable<K>, K : Number, V : Number, V : Comparable<V> {
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