package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.plusValueInterpolate

fun List<Map<Long, Double>>.filterOverlap(): List<Map<Long, Double>>{
    val min = mapNotNull { it.keys.min() }.max()!!
    val max = mapNotNull { it.keys.max() }.min()!!

    return map { series ->
        series.plusValueInterpolate(min)
            .plusValueInterpolate(max)
            .filter { entry -> entry.key in min..max }
    }
}