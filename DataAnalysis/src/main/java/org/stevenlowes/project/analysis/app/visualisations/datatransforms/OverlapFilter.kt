package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.plusValueInterpolate

fun List<Series>.filterOverlap(): List<Series> {
    val min = mapNotNull { it.data.keys.min() }.max()!!
    val max = mapNotNull { it.data.keys.max() }.min()!!

    return map { series ->
        val newData = series.data.plusValueInterpolate(min)
            .plusValueInterpolate(max)
            .filter { entry -> entry.key in min..max }

        val newLabels = series.labels.filter { it.x in min..max }

        return@map Series(
            series.name + " (Overlap Filtered)",
            series.color,
            newData,
            newLabels
        )
    }
}