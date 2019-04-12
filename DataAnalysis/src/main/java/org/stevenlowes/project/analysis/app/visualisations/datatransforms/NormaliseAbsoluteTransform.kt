package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.mapY

fun Series.transformNormaliseAbsolute(newName: String? = null, newColor: Color? = null): Series {
    val subtract = data.getValueInterpolate(0L)!!
    val newData = data.mapValues { it.value - subtract }
    val newLabels = labels.mapY { it.y - subtract }
    return Series(
        newName ?: "$name (NormaliseAbs)",
        newColor ?: color,
        newData,
        newLabels
    )
}

fun List<Series>.transformNormaliseAbsolute(): List<Series> = map { it.transformNormaliseAbsolute() }