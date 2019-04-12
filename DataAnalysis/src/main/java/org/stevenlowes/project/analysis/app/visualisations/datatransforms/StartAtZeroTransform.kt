package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.mapX

fun Series.transformStartAtZero(newName: String? = null, newColor: Color? = null): Series {
    val subtract = data.keys.min()!!
    val newData = data.mapKeys { it.key - subtract }
    val newLabels = labels.mapX { it.x - subtract }

    return Series(
        newName ?: "$name (Start at Zero)",
        newColor ?: color,
        newData,
        newLabels
    )
}