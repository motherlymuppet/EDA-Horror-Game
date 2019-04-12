package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.mapY
import org.stevenlowes.project.analysis.times

fun Series.transformNormaliseRelative(newName: String? = null, newColor: Color? = null): Series {
    val valueAtZero = data.getValueInterpolate(0L)!!
    val multiplicand = 1.0 / valueAtZero

    val newData = data * multiplicand
    val newLabels = labels.mapY { it.y * multiplicand }

    return Series(
        newName ?: "$name (NormaliseRel)",
        newColor ?: color,
        newData,
        newLabels
    )
}

fun List<Series>.transformNormaliseRelative(): List<Series> = map { it.transformNormaliseRelative() }