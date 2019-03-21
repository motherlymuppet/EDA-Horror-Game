package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series

fun <V> Map<Long, V>.transformStartAtZero(): Map<Long, V> {
    val start = keys.min()!!
    return mapKeys { it.key - start }
}