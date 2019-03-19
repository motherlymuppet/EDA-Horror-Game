package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.square

fun Map<Long, Double>.transformGraident(): Map<Long, Double>{
    val list = toList().sortedBy { it.first }
    return (0 until (size-1)).map {index ->
        val (x1, y1) = list[index]
        val (x2, y2) = list[index + 1]

        val yDelta = y2 - y1
        val xDelta = x2 - x1
        return@map x1 to yDelta / xDelta
    }.toMap()
}