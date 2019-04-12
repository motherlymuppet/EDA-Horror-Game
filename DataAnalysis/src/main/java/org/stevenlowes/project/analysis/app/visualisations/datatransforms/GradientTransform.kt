package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.mapY

fun Series.transformGraident(newName: String?, color: Color?): Series {
    val list = data.toList().sortedBy { it.first }
    val newData = (0 until (data.size - 1)).map { index ->
        val (x1, y1) = list[index]
        val (x2, y2) = list[index + 1]

        val yDelta = y2 - y1
        val xDelta = x2 - x1
        return@map x1 to yDelta / xDelta
    }.toMap()

    val newLabels = labels.mapY {
        newData.getValueInterpolate(it.x)!!
    }

    return Series(
        newName ?: "$name (Gradient)",
        color,
        newData,
        newLabels
    )
}