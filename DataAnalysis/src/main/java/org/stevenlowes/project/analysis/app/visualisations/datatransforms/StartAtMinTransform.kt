package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series

fun Map<Long, Double>.transformStartAtMin(): Map<Long, Double> {
    val start = minBy{it.value}!!
    return mapKeys { it.key - start.key }
}