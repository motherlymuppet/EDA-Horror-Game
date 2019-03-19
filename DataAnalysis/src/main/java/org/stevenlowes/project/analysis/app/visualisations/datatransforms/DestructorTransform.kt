package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import kotlin.math.max

fun Map<Long, Double>.transformDestructor(totalPoints: Int): Map<Long, Double> {
    val keepEvery = max(size/totalPoints, 1)
    val keepKeys = keys.asSequence().sorted().withIndex().filter { it.index % keepEvery == 0 }.map { it.value }.toSet()
    return filterKeys { it in keepKeys }
}