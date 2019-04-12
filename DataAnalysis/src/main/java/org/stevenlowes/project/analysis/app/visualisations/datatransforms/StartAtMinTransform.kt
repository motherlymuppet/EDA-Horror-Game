package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.mapX

fun Series.transformStartAtMin(newName: String? = null, newColor: Color? = null): Series {
    val (subtract, _) = data.minBy { it.value }!!
    val newData = data.mapKeys { it.key - subtract }
    val newLabels = labels.mapX { it.x - subtract }

    return Series(
        newName ?: "$name (Start at Min)",
        newColor ?: color,
        newData,
        newLabels
    )
}