package org.stevenlowes.project.analysis.app.visualisations.datatransforms

fun Map<Long, Double>.transformStartAtZero(): Map<Long, Double> {
    val start = keys.min()!!
    return mapKeys { it.key - start }
}