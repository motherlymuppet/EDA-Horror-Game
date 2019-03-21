package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.interpolate
import org.stevenlowes.project.analysis.times

fun Map<Long, Double>.transformNormaliseRelative(): Map<Long, Double>{
    val valueAtZero = getValueInterpolate(0L)!!
    val multiplicand = 1.0 / valueAtZero
    return this * multiplicand
}
